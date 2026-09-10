package com.connorjensen.griddlers.testpackage;

// Formatter fixture: valid names and logic, deliberately inconsistent layout.
public enum TestEnum
{
        RED(true),YELLOW(true),BLUE(false);

    private final boolean warm;

    TestEnum (boolean warm)
    {
            this.warm=warm;
    }

    public boolean isWarm () {return warm;}

    public String label ()
    {
        switch (this) {
                case RED: return "red";
            case YELLOW:
                    return "yellow";
          case BLUE: return "blue";
            default: throw new IllegalStateException("Unexpected color: "+this);
        }
    }
}
