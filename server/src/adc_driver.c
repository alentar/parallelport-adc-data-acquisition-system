#include <stdio.h>
#include <stdlib.h>

#ifdef __linux__
#include <sys/io.h>
#include <sys/time.h>
#elif __APPLE__
#include <time.h>
#endif

#include <adc_driver.h>

//-------Control Byte-------------------------------

// Single ended Mode
int ch0_DIN[8] = {1, 0, 0, 0, 1, 1, 1, 0};
int ch1_DIN[8] = {1, 0, 0, 1, 1, 1, 1, 0};
int ch2_DIN[8] = {1, 0, 1, 0, 1, 1, 1, 0};
int ch3_DIN[8] = {1, 0, 1, 1, 1, 1, 1, 0};
int ch4_DIN[8] = {1, 1, 0, 0, 1, 1, 1, 0};
int ch5_DIN[8] = {1, 1, 0, 1, 1, 1, 1, 0};
int ch6_DIN[8] = {1, 1, 1, 0, 1, 1, 1, 0};
int ch7_DIN[8] = {1, 1, 1, 1, 1, 1, 1, 0};

void lp_init(short num)
{ // initialize the port
#ifdef __linux__
    switch (lp_num)
    {
    case 2:
        lp_base_addr = 0x3BC;
        break;
    case 1:
        lp_base_addr = 0x278;
        break;
    default:
        lp_base_addr = 0x378;
        break;
    }
    image_data = save_data = inb(lp_base_addr);
    image_control = save_control = inb(lp_base_addr + 2);
    outb((image_control &= 0xEF), lp_base_addr + control_offset);
#elif __APPLE__
    time_t t;

    /* Intializes random number generator */
    srand((unsigned)time(&t));
#endif
}
void lp_restore()
{ // restore the port
#ifdef __linux__
    outb(save_data, lp_base_addr);
    outb(save_control, lp_base_addr + control_offset);
#endif
}

#ifdef __linux__
//---------check SSTRB pin------------------------------
//PURPOSE - check the SSTRB pin (pin 10 or S6)

int check_SSTRB_pin()
{
    if (inb(lp_base_addr + status_offset) & 0x40)
        return (high);
    else
        return (low);
}

//---------check DOUT pin------------------------------
//PURPOSE - check the SSTRB pin (pin 11 or S7)

int check_DOUT_pin()
{
    if (inb(lp_base_addr + status_offset) & 0x80)
        return (low);
    else
        return (high);
}
#endif

//---------two power of-----------------------------
//PURPOSE - to output two to the power of (x) - 2^x

int twopowerof(int x)
{
    int tempPow = 0;
    int value = 1;
    while (x > tempPow)
    {
        value = 2 * value;
        tempPow++;
    }
    return (value);
}

void clock_in_8bits(int buffDIN[8])
{
#ifdef __linux__
    for (count = 0; count <= 7; count++)
    {
        //---falling edge-------------------------
        if (buffDIN[count] == 1)
        {
            outb(DIN1, lp_base_addr); //SCLK-low, DIN-high
        }
        else
        {
            outb(all_low, lp_base_addr); //SCLK-low, DIN-low
        }

        //---rising edge---------------------------
        if (buffDIN[count] == 1)
        {
            if (count == 8)
                outb(DIN1_SCLK1_CS1, lp_base_addr);
            else
                outb(DIN1_SCLK1, lp_base_addr); //SCLK-high, DIN-high
        }
        else
        {
            if (count == 8)
                outb(SCLK1_CS1, lp_base_addr);
            else
                outb(SCLK1, lp_base_addr); //SCLK-high, DIN-low
        }
    }

    outb(CS1, lp_base_addr); //last falling edge so CS goes high
#elif __APPLE__
    int i;
    for (i = 0; i < 8; i++)
    {
        printf("signal [%d]\n", buffDIN[i]);
    }
#endif
}
// give input signal of 8 bits}
int clock_out_signal()
{
    // read 12-bits from ADC after conversion

#ifdef __linux__
    int tempOut[12] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    for (count = 1; count <= 14; count++)
    {
        //-----begin clocking out out byte----
        outb(0, lp_base_addr); //force pin2 SCLK to low

        //----rising edge------
        if (count == 1 && check_DOUT_pin() != 0)
        {
        }

        if (count >= 2 && count <= 13)
        {
            if (check_DOUT_pin() == high)
                tempOut[count - 2] = 1;
            else
                tempOut[count - 2] = 0;
        }

        outb(SCLK1, lp_base_addr); //force pin2 SCLK to high
    }

    outb(0, lp_base_addr); //force pin2 SCLK to low
    int dec = 0;
    for (count = 0; count <= 11; count++)
    {
        dec = ((tempOut[11 - count]) * (twopowerof(count))) + dec;
    }

    return (dec);
#elif __APPLE__
    return rand() % 4096;
#endif
}

int read_channel(int chan)
{
    int *control_bits;
    switch (chan)
    {
    case 0:
        control_bits = ch0_DIN;
        break;

    case 1:
        control_bits = ch1_DIN;
        break;

    case 2:
        control_bits = ch2_DIN;
        break;

    case 3:
        control_bits = ch3_DIN;
        break;

    case 4:
        control_bits = ch4_DIN;
        break;

    case 5:
        control_bits = ch5_DIN;
        break;

    case 6:
        control_bits = ch6_DIN;
        break;

    case 7:
        control_bits = ch7_DIN;
        break;

    default:
        return -1;
    }

#ifdef __linux__
    outb(CS1, lp_base_addr); //force CS (D2) high rest of the pins are low

    //---starting 8-bit command---
    outb(all_low, lp_base_addr);  //make all pins low
    outb(DIN1, lp_base_addr);     //input DIN START(high) bit
    clock_in_8bits(control_bits); //clocking in the 8 bit command
    return clock_out_signal();    //clocking out 12 bit signal from ADC
#elif __APPLE__
    clock_in_8bits(control_bits);
    return clock_out_signal();
#endif
}
