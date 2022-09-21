package hu.progmatic.springbank.dto;

import jakarta.validation.constraints.Size;

public class AccountDTO {

    @Size(min = 1, max = 10)
    private String name;

    private String number;

    private boolean premium;

    public AccountDTO() {

    }

    public AccountDTO(String name, String number, boolean premium) {
        this.name = name;
        this.number = number;
        this.premium = premium;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }
}
