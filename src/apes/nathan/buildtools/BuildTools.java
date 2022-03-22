package apes.nathan.buildtools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 *
 * My Build Utility
 *
 * Created by Nathan on 5/20/2017.
 *
 * -Re-Archived on 4/19/18
 */
public class BuildTools {
    public static void main(String args[]){

        File buildFolder = new File(args[0]);

        if(!(buildFolder.listFiles().length == 0)) {

            File[] artifactsArray = buildFolder.listFiles();
            ArrayList<File> artifacts = new ArrayList<>();

            for (int i = 0; i < artifactsArray.length; i++)
                artifacts.add(artifactsArray[i]);

            ArrayList<BasicFileAttributes> attributes = new ArrayList<>();

            Path path;
            for (int i = 0; i < artifacts.size(); i++) {
                path = Paths.get(artifacts.get(i).toURI());
                try {
                    attributes.add(Files.readAttributes(path, BasicFileAttributes.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            ArrayList<BasicFileAttributes> sortingAttributes = new ArrayList<>();
            attributes.forEach(attribute -> sortingAttributes.add(attribute));

            do {
                for (int i = 0; i < sortingAttributes.size(); i++)
                    if ((i + 1) < sortingAttributes.size())
                        if (sortingAttributes.get(i).creationTime().compareTo(sortingAttributes.get(i + 1).creationTime()) < 0)
                            sortingAttributes.remove(i);
                if (sortingAttributes.size() == 3)
                    if (sortingAttributes.get(1).creationTime().compareTo(sortingAttributes.get(2).creationTime()) < 0)
                        sortingAttributes.remove(1);
                    else
                        sortingAttributes.remove(2);
                if (sortingAttributes.size() == 2)
                    if (sortingAttributes.get(0).creationTime().compareTo(sortingAttributes.get(1).creationTime()) < 0)
                        sortingAttributes.remove(0);
                    else
                        sortingAttributes.remove(1);
            } while (sortingAttributes.size() > 1);

            try {
                Path buildPath = Paths.get(artifacts.get(attributes.indexOf(sortingAttributes.get(0))).toURI());

                URI link = new URI("file:///Users/Nathan/Documents/TestingServer/plugins/" + args[1]);
                Path targetPath = Paths.get(link);

                Files.copy(buildPath, targetPath, REPLACE_EXISTING);

                Process process = Runtime.getRuntime().exec("java -jar spigot.jar");

                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;

                while ((line = input.readLine()) != null) {
                    System.out.println(line);
                }

                int exitValue = process.waitFor();
                System.out.println("Exited with error code " + exitValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else System.out.println("No Builds to be Tested...");
    }
}
