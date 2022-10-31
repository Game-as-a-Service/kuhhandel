package org.gaas.kuhhandel.auction.eum;

public enum Animal {
	HORSE(1,"horse","馬", 1000),
	CATTLE(2,"Cattle","牛", 800),
	DONKEY(3,"donkey","驢", 500),
	GOAT(4,"goat","山羊", 350),
	SHEEP(5,"sheep","羊", 250),
	GOOSE(6,"Goose","鵝", 40),
	ROOSTER(7,"Rooster","公雞", 10);

	
	Animal(int id,String type, String desc,int fraction) {
		this.id = id;
        this.type = type;
        this.desc = desc;
        this.fraction=fraction;
    }
	private int id;
    private String type;
    private String desc;
    private int fraction;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    public int getFraction() {
        return fraction;
    }

    public void setFraction(int fraction) {
        this.fraction = fraction;
    }
}
