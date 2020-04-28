package com.example.appproject_coronatracker.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryParcelable implements Parcelable {


    @SerializedName("flag")
    @Expose
    private String flag;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("countryInfo")
    @Expose
    private CountryInfo countryInfo;
    @SerializedName("cases")
    @Expose
    private Integer cases;
    @SerializedName("todayCases")
    @Expose
    private Integer todayCases;
    @SerializedName("deaths")
    @Expose
    private Integer deaths;
    @SerializedName("recovered")
    @Expose
    private Integer recovered;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("critical")
    @Expose
    private Integer critical;
    @SerializedName("tests")
    @Expose
    private Integer tests;

    private  CountryParcelable(Parcel in) {

        flag = in.readString();
        country = in.readString();
       // countryInfo = in.readInt();
        cases = in.readInt();
        todayCases = in.readInt();
        deaths = in.readInt();
        recovered = in.readInt();
        active = in.readInt();
        critical = in.readInt();
        tests = in.readInt();
    }

    public static final Creator<CountryParcelable> CREATOR = new Creator<CountryParcelable>() {
        @Override
        public CountryParcelable createFromParcel(Parcel in) {
            return new CountryParcelable(in);
        }

        @Override
        public CountryParcelable[] newArray(int size) {
            return new CountryParcelable[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getFlag());
        dest.writeString(getCountry());
        dest.writeString(getCountryInfo().getFlag());
        dest.writeInt(getCases());
        dest.writeInt(getTodayCases());
        dest.writeInt(getDeaths());
        dest.writeInt(getRecovered());
        dest.writeInt(getActive());
        dest.writeInt(getCritical());
        dest.writeInt(getTests());

    }

    public String getFlag() {
        return flag;
    }

    public String getCountry() {
        return country;
    }

    public CountryInfo getCountryInfo() {
        return countryInfo;
    }

    public Integer getCases() {
        return cases;
    }

    public Integer getTodayCases() {
        return todayCases;
    }

    public Integer getDeaths() {
        return deaths;
    }

    public Integer getRecovered() {
        return recovered;
    }

    public Integer getActive() {
        return active;
    }

    public Integer getCritical() {
        return critical;
    }

    public Integer getTests() {
        return tests;
    }
}
