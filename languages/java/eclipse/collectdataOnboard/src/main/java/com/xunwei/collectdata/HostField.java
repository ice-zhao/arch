package com.xunwei.collectdata;

public class HostField {
    //Temperature Humidity
    public static final int Temperature = 2;
    public static final int Humidity = 3;

    //3-phases ammeter
    public static final int UA = 31;
    public static final int UB = 32;
    public static final int UC = 33;
    public static final int UAB = 34;
    public static final int UBC = 35;
    public static final int UCA = 36;
    public static final int VolUnbalance = 37;
    public static final int IA = 39;
    public static final int IB = 40;
    public static final int IC = 41;
    public static final int Curunbalance = 42;
    public static final int PA = 44;
    public static final int PB = 45;
    public static final int PC = 46;
    public static final int PZ = 47;
    public static final int QA = 49;
    public static final int QB = 50;
    public static final int QC = 51;
    public static final int QZ = 52;
    public static final int SA = 54;
    public static final int SB = 55;
    public static final int SC = 56;
    public static final int SZ = 57;
    public static final int PFA = 59;
    public static final int PFB = 60;
    public static final int PFC = 61;
    public static final int PFZ = 62;
    public static final int FRQ = 63;
    public static final int Epwr_in = 65;   //输入侧有功电能度数
    public static final int Epwr_out = 66;  //输出侧有功电能度数
    public static final int Eq_in = 67;     //输入侧无功电能度数
    public static final int Eq_out = 68;    //输出侧无功电能度数

    //single phase ammeter
    public static final int PhaseVoltage = 69;
    public static final int PhaseCurrent = 70;
    public static final int Power = 71;     //有功功率
    public static final int QPower = 72;    //无功功率
    public static final int PowerPF = 73;   //功率因数PF
    public static final int SystemFrq = 74;
    public static final int EPWR = 75;      //有功电能度数
    public static final int ERQ = 76;   //无功电能度数

    //flowmeter
    public static final int WorkTemperature = 662;
    public static final int Pressure = 663;
    public static final int InstantFlowRate = 664;
    public static final int TotalFlowRate = 665;
    //hongRun flowmeter
    public static final int HRInstantFlowRate = 666;
    public static final int HRTotalFlowRate = 667;
    //KSD Tube flowmeter
    public static final int TubeWorkTemperature = 668;
    public static final int TubeWorkPressure = 669;
    public static final int Tube1InstantFlowRate = 670;
    public static final int Tube1TotalFlowRate = 671;
    public static final int Tube2InstantFlowRate = 672;
    public static final int Tube2TotalFlowRate = 673;

    //PM25 device
    public static final int CO2=682;
    public static final int TVOC=683;       //挥发型气体
    public static final int CH2O=684;       //甲醛
    public static final int PM25=685;
    public static final int PM25Humidity=686;
    public static final int PM25Temperature=687;
    public static final int PM10=688;

}
