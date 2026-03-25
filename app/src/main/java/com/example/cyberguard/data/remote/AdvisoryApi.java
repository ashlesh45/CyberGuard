package com.example.cyberguard.data.remote;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface AdvisoryApi {
    @GET("advisories") // Example endpoint
    Call<List<AdvisoryDto>> getLatestAdvisories();

    class AdvisoryDto {
        public String id;
        public String title;
        public String content;
        public long timestamp;
        public String source;
    }
}
