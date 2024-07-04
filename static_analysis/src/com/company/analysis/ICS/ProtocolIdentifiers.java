package com.company.analysis.ICS;

import java.util.HashMap;

/**
 * API and UI token identifiers for all industrial protocols.
 */
public class ProtocolIdentifiers {
    private static HashMap<String, HashMap<String, String[]>> protocolIdentifiers = new HashMap<>();

    /**
     * Fill in API and UI Token identifiers for each protocols
     * @param protocolIdentifiers static identifiers
     */
    public static void fillIdentifiers(HashMap<String, HashMap<String, String[]>> protocolIdentifiers) {
        String[] APIs = new String[] {"HttpURLConnection", "DefaultHttpClient"};
        String[] Tokens = new String[] {"http://"};
        HashMap<String, String[]> identifiers = new HashMap<>();

        identifiers.put("APIs", APIs);
        identifiers.put("Tokens", Tokens);
        protocolIdentifiers.put("Http", identifiers);

        APIs = new String[] {"HttpsURLConnection"};
        Tokens = new String[] {"https://"};
        identifiers = new HashMap<String, String[]>();
        identifiers.put("APIs", APIs);
        identifiers.put("Tokens", Tokens);
        protocolIdentifiers.put("Https", identifiers);

        APIs = new String[] {"readCoils", "writeCoil", "writeMultipleCoils", "readInputDiscretes", "readInputRegisters", "readMultipleRegisters", "writeSingleRegister", "writeMultipleRegisters", "maskWriteRegister"};
        Tokens = new String[] {"Modbus", "Modbus TCP", "Modbus TCP Client", "Port", "RTU ID", "Rows", "Coils", "Coil Status", "Input", "Input Status", "Input Register", "Input Reg", "Holding Register", "Holding Reg", "502", "Port 502", "Write single coil", "Write single register", "Write multiple coils", "Write multiple registers"};
        identifiers = new HashMap<String, String[]>();
        identifiers.put("APIs", APIs);
        identifiers.put("Tokens", Tokens);
        protocolIdentifiers.put("Modbus TCP", identifiers);

        APIs = new String[] {"S7Connection", "Libnodave", "addVarToReadRequest", "addBitVarToReadRequest", "prepareReadRequest", "prepareWriteRequest", "addVarToWriteRequest", "addBitVarToWriteRequest", "execReadRequest", "Moka7", "S7Client", "RecvPacket", "SendPacket", "ISOConnect", "NegotiatePduLength", "SetConnectionParams", "ReadArea", "WriteArea", "GetAgBlockInfo", "DBGet", "ReadSZL"};
        Tokens = new String[] {"102", "Port 102", "S7 300", "S7 400", "S7 1200", "S7 1500", "Rack", "Slot", "S7 PLC", "S7 Client"};
        identifiers = new HashMap<String, String[]>();
        identifiers.put("APIs", APIs);
        identifiers.put("Tokens", Tokens);
        protocolIdentifiers.put("S7", identifiers);

        APIs = new String[] {};
        Tokens = new String[] {"Ethernet/IP"};
        identifiers = new HashMap<String, String[]>();
        identifiers.put("APIs", APIs);
        identifiers.put("Tokens", Tokens);
        protocolIdentifiers.put("Ethernet/IP", identifiers);

        APIs = new String[] {};
        Tokens = new String[] {"OPC UA", "OPCUA"};
        identifiers = new HashMap<String, String[]>();
        identifiers.put("APIs", APIs);
        identifiers.put("Tokens", Tokens);
        protocolIdentifiers.put("OPC UA", identifiers);

        APIs = new String[] {};
        Tokens = new String[] {"Fins", "Omron", "9600", "Port 9600"};
        identifiers = new HashMap<String, String[]>();
        identifiers.put("APIs", APIs);
        identifiers.put("Tokens", Tokens);
        protocolIdentifiers.put("Fins", identifiers);

        APIs = new String[] {};
        Tokens = new String[] {"MQTT", "1883", "Port 1883", "8883", "Port 8883"};
        identifiers = new HashMap<String, String[]>();
        identifiers.put("APIs", APIs);
        identifiers.put("Tokens", Tokens);
        protocolIdentifiers.put("MQTT", identifiers);
    }

    public ProtocolIdentifiers() {
        fillIdentifiers(protocolIdentifiers);
    }

    public HashMap<String, HashMap<String, String[]>> getProtocolIdentifiers() {
        return protocolIdentifiers;
    }
}
