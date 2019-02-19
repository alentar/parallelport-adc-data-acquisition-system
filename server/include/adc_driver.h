#ifndef __ADC_DRIVER_H__
#define __ADC_DRIVER_H__

//------- Parallel port data.
static short lp_base_addr; // save base addr of pp port.
#define status_offset 1
#define control_offset 2
#define high 1
#define low 0

static char save_data; // save of original values.
static char save_control;
static char image_data; // use image as master record of port.
static char image_control;

int count; // used for clock data in or out

//-------Parallel port pins-------------------------
#define CS1 0x02
#define SCLK1 0x01
#define SCLK1_CS1 0x03
#define DIN1 0x04
#define DIN1_SCLK1 0x05
#define DIN1_SCLK1_CS1 0x07
#define all_low 0x00

#define DIFFERENTIAL 0

void lp_init(short num); // initialize the port
void lp_restore(); // restore the port

void clock_in_8bits(int buffDIN[8]); // give input signal of 8 bits
int clock_out_signal(); // read 12-bits from ADC after conversion
void change_mode(int sgl); // change mode to single_ended(1) or differential(0)
int read_channel(int chan); // read a channel 0 - 7

#endif