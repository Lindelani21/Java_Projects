package com.bank.account.service;

import java.io.*;

public class SerializationUtility {
    public static void serialize(Object obj, String fileName) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(fileName))) {
            oos.writeObject(obj);
        }
    }

    public static Object deserialize(String fileName) 
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(fileName))) {
            return ois.readObject();
        }
    }
}