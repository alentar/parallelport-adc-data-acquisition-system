#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#include <adc_driver.h>

//-------Control Byte-------------------------------

int mode = SINGLE_ENDED;

// Single ended Mode
int ch0_DIN[8] = {1, 0, 0, 0, 1, 1, 1, 0};
int ch1_DIN[8] = {1, 0, 0, 1, 1, 1, 1, 0};
int ch2_DIN[8] = {1, 0, 1, 0, 1, 1, 1, 0};
int ch3_DIN[8] = {1, 0, 1, 1, 1, 1, 1, 0};
int ch4_DIN[8] = {1, 1, 0, 0, 1, 1, 1, 0};
int ch5_DIN[8] = {1, 1, 0, 1, 1, 1, 1, 0};
int ch6_DIN[8] = {1, 1, 1, 0, 1, 1, 1, 0};
int ch7_DIN[8] = {1, 1, 1, 1, 1, 1, 1, 0};

// Differential Mode + -
int ch0_1_DIN[8] = {1, 0, 0, 0, 1, 0, 1, 0};
int ch2_3_DIN[8] = {1, 0, 0, 1, 1, 0, 1, 0};
int ch4_5_DIN[8] = {1, 0, 1, 0, 1, 0, 1, 0};
int ch6_7_DIN[8] = {1, 0, 1, 1, 1, 0, 1, 0};
int ch1_0_DIN[8] = {1, 1, 0, 0, 1, 0, 1, 0};
int ch3_2_DIN[8] = {1, 1, 0, 1, 1, 0, 1, 0};
int ch5_4_DIN[8] = {1, 1, 1, 0, 1, 0, 1, 0};
int ch7_6_DIN[8] = {1, 1, 1, 1, 1, 0, 1, 0};

void lp_init(short num)
{ // initialize the port
    time_t t;

    /* Intializes random number generator */
    srand((unsigned)time(&t));
}
void lp_restore()
{ // restore the port
}

void clock_in_8bits(int buffDIN[8])
{
    int i;
    for(i = 0; i < 8; i++)
    {
        printf("signal [%d]\n", buffDIN[i]);
    }
    
}
// give input signal of 8 bits}
int clock_out_signal()
{
    // read 12-bits from ADC after conversion
    return rand() % 4096;
}

void change_mode(int sgl)
{
    mode = sgl;
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
        break;
    }

    clock_in_8bits(control_bits);
    return clock_out_signal();
}
