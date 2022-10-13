public class WyschlaKrynicaMadrosciException
    extends IndexOutOfBoundsException{

    public WyschlaKrynicaMadrosciException(){
        super();
        System.out.println("Niestety krynica mądrości już wyschła. Nie znaleziono kolejnego cytatu...");
    }
}
