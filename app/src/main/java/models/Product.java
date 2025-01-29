package models;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("code")
    private String code;

    @SerializedName("product")
    private ProductDetails product;

    @SerializedName("status")
    private int status;

    @SerializedName("status_verbose")
    private String statusVerbose;

    @SerializedName("nutriments")
    private Nutriments nutriments;

    public Nutriments getNutriments() {
        return nutriments;
    }

    public void setNutriments(Nutriments nutriments) {
        this.nutriments = nutriments;
    }

    // Gettery i settery

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ProductDetails getProduct() {
        return product;
    }

    public void setProduct(ProductDetails product) {
        this.product = product;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusVerbose() {
        return statusVerbose;
    }

    public void setStatusVerbose(String statusVerbose) {
        this.statusVerbose = statusVerbose;
    }

    public String toString() {
        return "Product{" +
                "productName='" + product.getProductName() + '\'' +
                ", brands='" + product.getBrands() + '\'' +
                ", energyKcal100g=" + (nutriments != null ? nutriments.getEnergyKcal100g() : "N/A") +
                ", fat100g=" + (nutriments != null ? nutriments.getFat100g() : "N/A") +
                ", saturatedFat100g=" + (nutriments != null ? nutriments.getSaturatedFat100g() : "N/A") +
                ", carbohydrates100g=" + (nutriments != null ? nutriments.getCarbohydrates100g() : "N/A") +
                ", sugars100g=" + (nutriments != null ? nutriments.getSugars100g() : "N/A") +
                ", fiber100g=" + (nutriments != null ? nutriments.getFiber100g() : "N/A") +
                ", proteins100g=" + (nutriments != null ? nutriments.getProteins100g() : "N/A") +
                ", salt100g=" + (nutriments != null ? nutriments.getSalt100g() : "N/A") +
                '}';
    }

    public static class ProductDetails {

        @SerializedName("ingredients_text")
        private String ingredientsText;

        @SerializedName("product_name")
        private String productName;

        @SerializedName("brands")
        private String brands;

        @SerializedName("categories")
        private String categories;

        @SerializedName("nutriments")
        private Nutriments nutriments;

        public Nutriments getNutriments() {
            return nutriments;
        }

        public void setNutriments(Nutriments nutriments) {
            this.nutriments = nutriments;
        }

//        @SerializedName("nutriments")
//        private Nutriments nurtiments;

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getBrands() {
            return brands;
        }

        public void setBrands(String brands) {
            this.brands = brands;
        }

        public String getCategories() {
            return categories;
        }

        public void setCategories(String categories) {
            this.categories = categories;
        }
    }

    // Klasa Nutriments
    public static class Nutriments {
        @SerializedName("energy-kcal_100g")
        private Double energyKcal100g; // Energia w kcal na 100g

        @SerializedName("fat_100g")
        private Double fat100g; // Tłuszcz na 100g

        @SerializedName("saturated-fat_100g")
        private Double saturatedFat100g; // Tłuszcze nasycone na 100g

        @SerializedName("carbohydrates_100g")
        private Double carbohydrates100g; // Węglowodany na 100g

        @SerializedName("sugars_100g")
        private Double sugars100g; // Cukry na 100g

        @SerializedName("fiber_100g")
        private Double fiber100g; // Błonnik na 100g

        @SerializedName("proteins_100g")
        private Double proteins100g; // Białko na 100g

        @SerializedName("salt_100g")
        private Double salt100g; // Sól na 100g

        // Gettery i settery
        public Double getEnergyKcal100g() {
            return energyKcal100g;
        }

        public void setEnergyKcal100g(Double energyKcal100g) {
            this.energyKcal100g = energyKcal100g;
        }

        public Double getFat100g() {
            return fat100g;
        }

        public void setFat100g(Double fat100g) {
            this.fat100g = fat100g;
        }

        public Double getSaturatedFat100g() {
            return saturatedFat100g;
        }

        public void setSaturatedFat100g(Double saturatedFat100g) {
            this.saturatedFat100g = saturatedFat100g;
        }

        public Double getCarbohydrates100g() {
            return carbohydrates100g;
        }

        public void setCarbohydrates100g(Double carbohydrates100g) {
            this.carbohydrates100g = carbohydrates100g;
        }

        public Double getSugars100g() {
            return sugars100g;
        }

        public void setSugars100g(Double sugars100g) {
            this.sugars100g = sugars100g;
        }

        public Double getFiber100g() {
            return fiber100g;
        }

        public void setFiber100g(Double fiber100g) {
            this.fiber100g = fiber100g;
        }

        public Double getProteins100g() {
            return proteins100g;
        }

        public void setProteins100g(Double proteins100g) {
            this.proteins100g = proteins100g;
        }

        public Double getSalt100g() {
            return salt100g;
        }

        public void setSalt100g(Double salt100g) {
            this.salt100g = salt100g;
        }

        // Dodatkowe metody do zwracania wartości odżywczych
        public Double getEnergyKcal() {
            return energyKcal100g;
        }

        public Double getFat() {
            return fat100g;
        }

        public Double getCarbohydrates() {
            return carbohydrates100g;
        }

        public Double getProteins() {
            return proteins100g;
        }
    }
}
