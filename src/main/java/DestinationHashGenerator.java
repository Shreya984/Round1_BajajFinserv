import java.io.FileReader;
import java.security.MessageDigest;
import java.util.Random;

import org.json.JSONObject;

class DestinationHashGenerator {
    private static String findKey(JSONObject jsonObject, String key) {
        for (String currentKey : jsonObject.keySet()) {
            Object value = jsonObject.get(currentKey);
            if (currentKey.equals(key)) {
                return value.toString();
            } else if (value instanceof JSONObject) {
                String result = findKey((JSONObject) value, key);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar <jar_file_name> <roll_number> <path_to_json_file>");
            return;
        }

        String rollNumber = args[0].toLowerCase().replaceAll("\\s", "");
        String filePath = args[1];
        String destinationValue;

        try {
            FileReader reader = new FileReader(filePath);
            StringBuilder jsonContent = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                jsonContent.append((char) c);
            }
            reader.close();

            JSONObject jsonObject = new JSONObject(jsonContent.toString());

            destinationValue = findKey(jsonObject, "destination");
            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                return;
            }

            String randomString = generateRandomString(8);
            String toHash = rollNumber + destinationValue + randomString;
            String hashedValue = generateMD5Hash(toHash);

            System.out.println(hashedValue + ";" + randomString);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
