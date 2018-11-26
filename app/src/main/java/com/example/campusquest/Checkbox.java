package com.example.campusquest;

public class Checkbox {
    private int position;
    public boolean isChecked;

    public Checkbox(int position, boolean isChecked) {
        this.isChecked = isChecked;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean getChecked(){
        return isChecked;
    }
}
