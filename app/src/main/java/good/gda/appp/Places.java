package good.gda.appp;

public class Places {

    String placeId, Name_Place,Type;
    double positionX,positionY,EXP;

    public Places(String PlaceId, String name_Place, double posX,
                  double posY, double exp, String type)
    {
        this.placeId = PlaceId;
        this.Name_Place = name_Place;
        this.positionX = posX;
        this.positionY = posY;
        this.EXP = exp;
        this.Type = type;
    }

    public String getPlaceId(){
        return placeId;
    }

    public String getName_Place(){
        return Name_Place;
    }

    public double getPositionX()
    { return positionX;}

    public double getPositionY()
    {return positionY;}

    public double getExp()
    {return EXP;}

    public String getType()
    {return Type;}
}
