package com.ecuadorcomparte.ecuador_comparte.dto;

import com.ecuadorcomparte.ecuador_comparte.model.ContactRequest;

public class ContactRequestDTO {

    private String name;
    private String email;
    private String phone;
    private ContactRequest.Purpose purpose;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public ContactRequest.Purpose getPurpose() { return purpose; }
    public void setPurpose(ContactRequest.Purpose purpose) { this.purpose = purpose; }
}
