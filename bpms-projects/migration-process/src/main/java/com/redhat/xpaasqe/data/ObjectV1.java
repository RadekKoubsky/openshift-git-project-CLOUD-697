package com.redhat.xpaasqe.data;

import org.kie.api.remote.Remotable;

import javax.xml.bind.annotation.XmlRootElement;

import java.util.UUID;

/**
 * This class was automatically generated by the data modeler tool.
 */
@Remotable
@XmlRootElement
public class ObjectV1 implements java.io.Serializable {

    static final long serialVersionUID = 1L;

    @org.kie.api.definition.type.Label(value = "uuid label")
    private String uuid;

    public ObjectV1() {
        this.uuid = UUID.randomUUID().toString();
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ObjectV1(String uuid) {
        this.uuid = uuid;
    }

}