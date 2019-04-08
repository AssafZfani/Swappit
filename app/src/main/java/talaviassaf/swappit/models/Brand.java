package talaviassaf.swappit.models;

import java.util.ArrayList;

import talaviassaf.swappit.utils.BrandsAdapter;

@SuppressWarnings("unused")

public class Brand {

    private String brand, hebrew, image, strip;
    private int id;
    private ArrayList<Integer> types;

    Brand() {

    }

    public Brand(String brand, String hebrew, String image, String strip, int id, ArrayList<Integer> types) {

        this.brand = brand;
        this.hebrew = hebrew;
        this.image = image;
        this.strip = strip;
        this.id = id;
        this.types = types;
    }

    public static Brand getBrandByName(String name) {

        for (Brand brand : BrandsAdapter.fullList)
            if (brand.getBrand().equalsIgnoreCase(name))
                return brand;

        return null;
    }

    public static ArrayList<String> getBrandsList(String text) {

        text = text.replace(text.charAt(0), String.valueOf(text.charAt(0)).toUpperCase().charAt(0));

        ArrayList<String> brands = new ArrayList<>();

        for (Brand brand : BrandsAdapter.fullList)
            if (brand.getBrand().contains(text) || brand.getHebrew().contains(text))
                brands.add(brand.getBrand() + " - " + brand.getHebrew());

        return brands;
    }

    public String getBrand() {

        return brand;
    }

    public String getHebrew() {

        return hebrew;
    }

    public String getImage() {

        return image;
    }

    public String getStrip() {

        return strip;
    }

    public int getId() {

        return id;
    }

    public ArrayList<Integer> getTypes() {

        return types;
    }
}
