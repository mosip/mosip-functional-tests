package io.mosip.testrig.apirig.admin.fw.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;

import io.mosip.testrig.apirig.testrunner.MosipTestRunner;

public class TestJava {
	private static final Logger lOGGER = Logger.getLogger(TestJava.class);
	private static final SecureRandom secureRandom = new SecureRandom();


    public static void main(String[] args) {
        String string = "e2e_getPingHealth(packetcreator)";
        Pattern pattern = Pattern.compile("(.*?)\\((.*?),(.*)\\)");
        Matcher matcher = pattern.matcher(string);
        if (matcher.matches()) {
            System.out.println("The string contains a comma between parentheses");
        } else {
            System.out.println("The string does not contain a comma between parentheses");
        }
    }



}
