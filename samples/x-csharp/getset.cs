class TimePeriod {
  int get;
  private double _seconds;
  
  int set;
  
  public double Seconds {
    get {
      return _seconds;
    }
    set {
      _seconds = value;
    }
  }
}

class TimePeriod2 {
  public double Hours { get; set; }
  
  public void foo() {
    get;
    set;
  };
  
  public double Minutes { get; set; }
  
  public void bar() {
    get;
    set;
  };
}
