package com.example.appproject_coronatracker.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Country implements Parcelable {

    @SerializedName("updated")
    @Expose
    private Double updated;
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
    @SerializedName("todayDeaths")
    @Expose
    private Integer todayDeaths;
    @SerializedName("recovered")
    @Expose
    private Integer recovered;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("critical")
    @Expose
    private Integer critical;
    @SerializedName("casesPerOneMillion")
    @Expose
    private Integer casesPerOneMillion;
    @SerializedName("deathsPerOneMillion")
    @Expose
    private Integer deathsPerOneMillion;
    @SerializedName("tests")
    @Expose
    private Integer tests;
    @SerializedName("testsPerOneMillion")
    @Expose
    private Integer testsPerOneMillion;
    @SerializedName("continent")
    @Expose
    private String continent;

    /**
     * No args constructor for use in serialization
     *
     */
    public Country() {
    }


    public Country(String country, CountryInfo countryInfo, Integer cases, Integer todayCases, Integer deaths, Integer todayDeaths, Integer recovered, Integer active, Integer critical, Integer tests) {
        super();
        this.country = country;
        this.countryInfo = countryInfo;
        this.cases = cases;
        this.todayCases = todayCases;
        this.deaths = deaths;
        this.todayDeaths = todayDeaths;
        this.recovered = recovered;
        this.active = active;
        this.critical = critical;
        this.tests = tests;
    }

    private Country(Parcel in) {
        if (in.readByte() == 0) {
            updated = null;
        } else {
            updated = in.readDouble();
        }
        country = in.readString();
        if (in.readByte() == 0) {
            cases = null;
        } else {
            cases = in.readInt();
        }
        if (in.readByte() == 0) {
            todayCases = null;
        } else {
            todayCases = in.readInt();
        }
        if (in.readByte() == 0) {
            deaths = null;
        } else {
            deaths = in.readInt();
        }
        if (in.readByte() == 0) {
            todayDeaths = null;
        } else {
            todayDeaths = in.readInt();
        }
        if (in.readByte() == 0) {
            recovered = null;
        } else {
            recovered = in.readInt();
        }
        if (in.readByte() == 0) {
            active = null;
        } else {
            active = in.readInt();
        }
        if (in.readByte() == 0) {
            critical = null;
        } else {
            critical = in.readInt();
        }
        if (in.readByte() == 0) {
            casesPerOneMillion = null;
        } else {
            casesPerOneMillion = in.readInt();
        }
        if (in.readByte() == 0) {
            deathsPerOneMillion = null;
        } else {
            deathsPerOneMillion = in.readInt();
        }
        if (in.readByte() == 0) {
            tests = null;
        } else {
            tests = in.readInt();
        }
        if (in.readByte() == 0) {
            testsPerOneMillion = null;
        } else {
            testsPerOneMillion = in.readInt();
        }
        continent = in.readString();
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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

    public Double getUpdated() {
        return updated;
    }

    public void setUpdated(Double updated) {
        this.updated = updated;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public CountryInfo getCountryInfo() {
        return countryInfo;
    }

    public void setCountryInfo(CountryInfo countryInfo) {
        this.countryInfo = countryInfo;
    }

    public Integer getCases() {
        return cases;
    }

    public void setCases(Integer cases) {
        this.cases = cases;
    }

    public Integer getTodayCases() {
        return todayCases;
    }

    public void setTodayCases(Integer todayCases) {
        this.todayCases = todayCases;
    }

    public Integer getDeaths() {
        return deaths;
    }

    public void setDeaths(Integer deaths) {
        this.deaths = deaths;
    }

    public Integer getTodayDeaths() {
        return todayDeaths;
    }

    public void setTodayDeaths(Integer todayDeaths) {
        this.todayDeaths = todayDeaths;
    }

    public Integer getRecovered() {
        return recovered;
    }

    public void setRecovered(Integer recovered) {
        this.recovered = recovered;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getCritical() {
        return critical;
    }

    public void setCritical(Integer critical) {
        this.critical = critical;
    }

    public Integer getCasesPerOneMillion() {
        return casesPerOneMillion;
    }

    public void setCasesPerOneMillion(Integer casesPerOneMillion) {
        this.casesPerOneMillion = casesPerOneMillion;
    }

    public Integer getDeathsPerOneMillion() {
        return deathsPerOneMillion;
    }

    public void setDeathsPerOneMillion(Integer deathsPerOneMillion) {
        this.deathsPerOneMillion = deathsPerOneMillion;
    }

    public Integer getTests() {
        return tests;
    }

    public void setTests(Integer tests) {
        this.tests = tests;
    }

    public Integer getTestsPerOneMillion() {
        return testsPerOneMillion;
    }

    public void setTestsPerOneMillion(Integer testsPerOneMillion) {
        this.testsPerOneMillion = testsPerOneMillion;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }


}
