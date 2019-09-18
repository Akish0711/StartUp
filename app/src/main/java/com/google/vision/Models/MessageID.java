package com.google.vision.Models;

import androidx.annotation.NonNull;

public class MessageID {
    public String messageID;

    public <T extends MessageID> T withID(@NonNull final String id){
        this.messageID = id;
        return (T) this;
    }
}
