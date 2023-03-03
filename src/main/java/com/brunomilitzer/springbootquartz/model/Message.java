package com.brunomilitzer.springbootquartz.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Message implements Serializable {

    private boolean valid;

    private String msg;

    private Object data;

    public Message( final boolean valid, final String msg ) {
        super();
        this.valid = valid;
        this.msg = msg;
    }

    public Message( final boolean valid ) {
        super();
        this.valid = valid;
    }

    public static Message failure( final String msg ) {
        return new Message( false, msg );
    }

    public static Message failure( final Exception e ) {
        return new Message( false, e.getMessage() );
    }

    public static Message failure() {
        return new Message( false );
    }

    public static Message success() {
        return new Message( true );
    }

    public static Message success( final String msg ) {
        return new Message( true, msg );
    }

    public void setData( final Object data ) {
        this.valid = true;
        this.data = data;
    }

}