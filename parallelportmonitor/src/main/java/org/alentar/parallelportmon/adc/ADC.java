package org.alentar.parallelportmon.adc;

public abstract class ADC {
    private int bitResolution;
    private String name;
    private int channels;
    private Double internalReferenceVoltage;
    private int resolution;

    public ADC(int bitResolution, String name, int channels, Double internalReferenceVoltage) {
        this.bitResolution = bitResolution;
        this.name = name;
        this.channels = channels;
        this.internalReferenceVoltage = internalReferenceVoltage;
        this.resolution = 1 << bitResolution;
    }

    public int getBitResolution() {
        return bitResolution;
    }

    public void setBitResolution(int bitResolution) {
        this.bitResolution = bitResolution;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public Double getInternalReferenceVoltage() {
        return internalReferenceVoltage;
    }

    public void setInternalReferenceVoltage(Double internalReferenceVoltage) {
        this.internalReferenceVoltage = internalReferenceVoltage;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public double voltageReading(int reading){
        return (double)reading*internalReferenceVoltage /(double)resolution;
    }

    @Override
    public String toString() {
        return "ADC{" +
                "bitResolution=" + bitResolution +
                ", name='" + name + '\'' +
                ", channels=" + channels +
                ", internalReferenceVoltage=" + internalReferenceVoltage +
                ", resolution=" + resolution +
                '}';
    }
}
