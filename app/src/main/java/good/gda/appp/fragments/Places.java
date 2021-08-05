package good.gda.appp.fragments;

public class Places {

    public String placeId, Name_Place,Type, desc;
    public double positionX,positionY,EXP;

    public Places(String PlaceId, String name_Place, double posX,
                  double posY, double exp, String type, String desc)
    {
        this.placeId = PlaceId;
        this.Name_Place = name_Place;
        this.positionX = posX;
        this.positionY = posY;
        this.EXP = exp;
        this.Type = type;
        this.desc = desc;
    }

}
