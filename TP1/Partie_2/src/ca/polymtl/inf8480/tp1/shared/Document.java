package ca.polymtl.inf8480.tp1.shared;

public class Document {

    public String name;
    public String checksum;
    public String content;

    public Document(String name, String checksum, String content){
        this.name = name;
        this.checksum = checksum ;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Document document = (Document) o;

        if (name != null ? !name.equals(document.name) : document.name != null) return false;
        return checksum != null ? checksum.equals(document.checksum) : document.checksum == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (checksum != null ? checksum.hashCode() : 0);
        return result;
    }
}
