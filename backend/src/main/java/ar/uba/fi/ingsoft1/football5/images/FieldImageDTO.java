package ar.uba.fi.ingsoft1.football5.images;

public class FieldImageDTO {
    private Long id;
    private byte[] data;

    public FieldImageDTO(Long id, byte[] data){
        this.id = id;
        this.data = data;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return id;
    }

    public void setData(byte[] data){
        this.data = data;
    }

    public byte[] getData(){
        return data;
    }
}
