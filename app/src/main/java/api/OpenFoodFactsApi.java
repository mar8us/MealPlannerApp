package api;
import models.Product;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OpenFoodFactsApi {
    @GET("product/{barcode}.json")
    Call<Product> getProduct(@Path("barcode") String barcode, @Query("lang") String lang);
}
