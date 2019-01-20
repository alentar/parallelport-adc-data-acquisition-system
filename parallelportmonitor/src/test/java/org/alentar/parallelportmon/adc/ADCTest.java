package org.alentar.parallelportmon.adc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ADCTest {
    @Test
    public void shouldReturnCorrectMinVoltage(){
        MAX186ADC max186ADC = new MAX186ADC();
        assertEquals(0, max186ADC.voltageReading(0), 0);
    }

    @Test
    public void shouldReturnCorrectMaxVoltage(){
        MAX186ADC max186ADC = new MAX186ADC();
        assertEquals(max186ADC.getInternalReferenceVoltage(), max186ADC.voltageReading(1<<12), 0);
    }
}
