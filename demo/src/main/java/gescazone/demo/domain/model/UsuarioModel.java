package gescazone.demo.domain.model;

public class UsuarioModel {

    private String id;
    private String numeroDocumento;
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
    private RolModel rol;
    private TipoDocumentoModel tipoDocumento;

    public UsuarioModel() {}

    public UsuarioModel(String numeroDocumento, String nombre, String apellido,
                        String correo, String contrasena, RolModel rol, TipoDocumentoModel tipoDocumento) {
        this.numeroDocumento = numeroDocumento;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
        this.tipoDocumento = tipoDocumento;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public RolModel getRol() { return rol; }
    public void setRol(RolModel rol) { this.rol = rol; }

    public TipoDocumentoModel getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumentoModel tipoDocumento) { this.tipoDocumento = tipoDocumento; }
}