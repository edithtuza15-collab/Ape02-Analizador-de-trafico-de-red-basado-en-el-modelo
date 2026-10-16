package modelo;

public class Paquete {
    private final int numeroLinea;
    private final CabeceraEthernet ethernet;
    private final CabeceraIP ip;
    private final CabeceraTransporte transporte;
    private final CabeceraAPP aplicacion;

    public Paquete(int numeroLinea, CabeceraEthernet ethernet, CabeceraIP ip,
                   CabeceraTransporte transporte, CabeceraAPP aplicacion) {
        this.numeroLinea = numeroLinea;
        this.ethernet = ethernet;
        this.ip = ip;
        this.transporte = transporte;
        this.aplicacion = aplicacion;
    }

    public int getNumeroLinea()              { return numeroLinea; }
    public CabeceraEthernet getEthernet()    { return ethernet; }
    public CabeceraIP getIp()                { return ip; }
    public CabeceraTransporte getTransporte() { return transporte; }
    public CabeceraAPP getAplicacion()       { return aplicacion; }

    public String getTipoTransporte()        { return transporte.getNombre(); }
    public String getProtocoloAplicacion()   { return aplicacion.getProtocolo(); }
    public int getPuertoDestino()            { return transporte.getPuertoDestino(); }

    public String describir() {
        return "Paquete (línea " + numeroLinea + ")\n"
                + ethernet.describir() + "\n"
                + ip.describir() + "\n"
                + transporte.describir() + "\n"
                + aplicacion.describir();
    }
}
