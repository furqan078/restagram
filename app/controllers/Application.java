package controllers;

import play.mvc.*;
import org.apache.commons.io.*;

import views.html.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.codec.digest.DigestUtils;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public static Result hi(){
            return ok("Hey ! What's cooking ?");
    }

    public static Result mirror(String path){
        if(!path.endsWith(".jpg") && !path.endsWith(".jpeg")){
            return badRequest("Only jpg/jpeg files are supported");
        }
        String pathHash = DigestUtils.md5Hex(path);
        try {
            FileUtils.copyURLToFile(new URL(path),new File("public/processed_images/"+pathHash));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ok(mirror.render("Original Image", "processed_images/" + pathHash));
    }


    public static Result effect(String path, String effect){
        /*if(!path.endsWith(".jpg") && !path.endsWith(".jpeg")){
            return badRequest("Only jpg/jpeg files are supported");
        }*/
        String pathHash = DigestUtils.md5Hex(path);
        String filePath = "public/processed_images/"+pathHash;
        try {
            FileUtils.copyURLToFile(new URL(path),new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("ERROR. Whoopsie !");
        }

        BufferedImage image;
        try {
            image = ImageIO.read(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("ERROR. This one's on me !");
        }

        if(effect.equals("invert")) {
            invert(image);
        }else if(effect.equals("grayscale")){
            grayscale(image);
        }else if (effect.equals("sketch")){
            grayscale(image);
            partial_differential(image);
        }else if (effect.equals("vintage")){
            vintage(image);
        }else{
            return badRequest("You can't just make up effect names. '"+effect+"' is not supported.");
        }

        String processedFileName = pathHash+"_"+effect+".png";
        String processedPath = "public/processed_images/"+processedFileName;


        try {
            ImageIO.write(image,"png",new File(processedPath));
        } catch (IOException e) {
            e.printStackTrace();
            return internalServerError("ERROR. I wrote this in my free time. What more do you want ?");
        }

        return ok(mirror.render(effect,"processed_images/" + processedFileName));
    }

    private static void vintage(BufferedImage image) {
        for (int i=0;i<image.getWidth();i++){
            for (int j=0;j<image.getHeight();j++){
                Color c = new Color(image.getRGB(i,j));
                int g = c.getGreen();
                int b = c.getBlue();
                int r = c.getRed();
                g = (int) (1.6 * g);
                b = (int) (0.4 * b);
                r = (int) (1.6 * r);
                g = g > 255 ? 255 : g;
                b = b > 255 ? 255 : b;
                r = r > 255 ? 255 : r;
                Color newc = new Color(r,g,b);
                int newrgb = newc.getRGB();
                image.setRGB(i,j,newrgb);
            }
        }
    }

    private static void partial_differential(BufferedImage image) {
        for (int i=0;i<image.getWidth()-3;i++){
            for (int j=0;j<image.getHeight()-3;j++){
                Color c = new Color(image.getRGB(i,j));
                Color cRight = new Color(image.getRGB(i+3,j+3));
                int gray = c.getGreen();
                int grayRight = cRight.getGreen();
                gray = grayRight - gray;
                gray = 255 - ( gray * 10);
                if(gray<0)
                    gray = 0;
                if(gray>255)
                    gray = 255;
                Color newc = new Color(gray,gray,gray);
                int newrgb = newc.getRGB();
                image.setRGB(i,j,newrgb);
            }
        }
    }

    private static void grayscale(BufferedImage image) {
        for (int i=0;i<image.getWidth();i++){
            for (int j=0;j<image.getHeight();j++){
                Color c = new Color(image.getRGB(i,j));
                int g = c.getGreen();
                int b = c.getBlue();
                int r = c.getRed();
                int gray = (int) (0.21 * r + 0.72 * g + 0.07 * b);
                Color newc = new Color(gray,gray,gray);
                int newrgb = newc.getRGB();
                image.setRGB(i,j,newrgb);
            }
        }
    }


    private static void invert(BufferedImage image){
        for (int i=0;i<image.getWidth();i++){
            for (int j=0;j<image.getHeight();j++){
                Color c = new Color(image.getRGB(i,j));
                int g = c.getGreen();
                int b = c.getBlue();
                int r = c.getRed();
                g = 255 - g;
                b = 255 - b;
                r = 255 - r;
                Color newc = new Color(r,g,b);
                int newrgb = newc.getRGB();
                image.setRGB(i,j,newrgb);
            }
        }
    }
}
