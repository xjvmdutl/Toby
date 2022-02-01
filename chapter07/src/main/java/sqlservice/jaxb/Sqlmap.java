package sqlservice.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sql" type="{http://www.epril.com/sqlmap}sqlType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD) //변환작업에서 참고할 정보를 어노테이션으로 가지고 있다
@XmlType(name = "sqlmapType", propOrder = {
    "sql"
})
@XmlRootElement(name = "sqlmap")
public class Sqlmap {

    @XmlElement(required = true)
    protected List<SqlType> sql;

    /**
     * Gets the value of the sql property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sql property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSql().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SqlType }
     *
     *
     */
    public List<SqlType> getSql() {
        if (sql == null) {
            sql = new ArrayList<SqlType>();
        }
        return this.sql;
    }

}
