IDIR =../include
CC=gcc
CFLAGS=-I$(IDIR)

ODIR=../obj
LDIR =../lib

LIBS=-lm

_DEPS = adc_driver.h
DEPS = $(patsubst %,$(IDIR)/%,$(_DEPS))

_OBJ = server.o adc_driver.o
OBJ = $(patsubst %,$(ODIR)/%,$(_OBJ))

$(ODIR)/%.o: %.c $(DEPS)
	$(CC) -c -o $@ $< $(CFLAGS)

all: clean lp_tty_start server

lp_tty_start:
	$(CC) -o lp_tty_start lp_tty_start.c

server: $(OBJ)
	$(CC) -o $@ $^ $(CFLAGS) $(LIBS)

.PHONY: clean

clean:
	rm -f $(ODIR)/*.o *~ core $(INCDIR)/*~