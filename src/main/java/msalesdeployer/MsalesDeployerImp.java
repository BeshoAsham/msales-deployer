package msalesdeployer; /**
 *
 */



import java.io.BufferedReader;
import java.io.File;

import java.io.FileReader;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;


/**
 * @author bishoys
 */
public class  MsalesDeployerImp {
    public static String readFileContent(File file) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        }
        return contentBuilder.toString();
    }

    public static Map<String, String> extractVariables(String fileContent) {
        Map<String, String> variables = new HashMap<>();
        String[] lines = fileContent.split("\n");
        for (String line : lines) {
            String[] parts = line.split("=");
            if (parts.length == 2) {
                variables.put(parts[0].trim(), parts[1].trim());
            }
        }
        return variables;
    }

}
