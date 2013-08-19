class Events: IDrawingObject {
  int add;
  event EventHandler PreDrawEvent;
  event EventHandler IDrawingObject.OnDraw {
    add {
      int add;
      lock(PreDrawEvent) {
        int add;
        PreDrawEvent += value;
      };
    }
    remove {
      lock(PreDrawEvent) {
        PreDrawEvent -= value;
      };
    }
  }
}
