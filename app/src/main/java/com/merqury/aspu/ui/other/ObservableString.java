package com.merqury.aspu.ui.other;

import java.util.ArrayList;
import java.util.List;

public class ObservableString {
    private String value;
    private final List<StringObserver> subscribers;

    public ObservableString(String value) {
        subscribers = new ArrayList<>();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        forceNotifyAll();
    }

    public void forceNotifyAll(){
        subscribers.forEach( (subscriber) -> {
            subscriber.onChange(value);
        });
    }

    public void subscribe(StringObserver subscriber){
        subscribers.add(subscriber);
    }

    public void unsubscribe(StringObserver subscriber){
        subscribers.remove(subscriber);
    }
}
