package com.ecuadorcomparte.ecuador_comparte.dto;

import com.ecuadorcomparte.ecuador_comparte.model.ContactRequest;

public class ContactRequestStatusDTO {

    private ContactRequest.Status status;

    public ContactRequest.Status getStatus() { return status; }
    public void setStatus(ContactRequest.Status status) { this.status = status; }
}
