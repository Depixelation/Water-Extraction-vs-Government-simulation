public class Event<T>{
    public final T obj;
    public final Type<T> type;
    private Event(Type<T> type, T val){
        this.type = type;
        obj = val;
    }

    public static <A> Event<A> of(Type<A> type, A val){
        return type.createEvent(val);
    }

    public static class Type<T>{
        public static final Type<Tank> TANK_ARRIVE = new Type<>();
        public static final Type<Void> REACH_CAP = new Type<>();

        private Event<T> createEvent(T val) {
            return new Event<>(this, val);
        }
    }
}
