package talaviassaf.swappit.models;

@SuppressWarnings("unused")

public class Voucher {

    private String address, barcode, brand, cvv, expDate, id, status, type;
    private int discount, price, value;
    private boolean firmProperty;
    private double distance;
    private RealLocation realLocation;

    Voucher() {

    }

    public Voucher(String address, String barcode, String brand, String cvv, int discount, String expDate, boolean firmProperty,
                   String id, RealLocation realLocation, int price, String status, String type, int value) {

        this.address = address;
        this.barcode = barcode;
        this.brand = brand;
        this.cvv = cvv;
        this.discount = discount;
        this.expDate = expDate;
        this.firmProperty = firmProperty;
        this.id = id;
        this.realLocation = realLocation;
        this.price = price;
        this.status = status;
        this.type = type;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Voucher && ((Voucher) obj).getId().equalsIgnoreCase(id);
    }

    public String getAddress() {

        return address;
    }

    public String getBarcode() {

        return barcode;
    }

    public String getBrand() {

        return brand;
    }

    public String getCvv() {

        return cvv;
    }

    public int getDiscount() {

        return discount;
    }

    public double getDistance() {

        return distance;
    }

    public void setDistance(double distance) {

        this.distance = distance;
    }

    public String getExpDate() {

        return expDate;
    }

    public boolean isFirmProperty() {

        return firmProperty;
    }

    public String getId() {

        return id;
    }

    public RealLocation getRealLocation() {

        return realLocation;
    }

    public int getPrice() {

        return price;
    }

    public String getStatus() {

        return status;
    }

    public String getType() {

        return type;
    }

    public int getValue() {

        return value;
    }
}