package com.example.myapplication.helper;

public class FeaturedHelperClass
{
    int image;
    String titile, desc;

    public FeaturedHelperClass(int image, String titile, String desc)
    {
        this.image = image;
        this.titile = titile;
        this.desc = desc;
    }

    public int getImage()
    {
        return image;
    }

    public String getTitle()
    {
        return titile;
    }

    public String getDescription()
    {
        return desc;
    }
}
