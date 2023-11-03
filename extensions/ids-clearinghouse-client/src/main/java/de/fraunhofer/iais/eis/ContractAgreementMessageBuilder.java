package de.fraunhofer.iais.eis;

import java.net.URI;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;


import de.fraunhofer.iais.eis.util.*;

public class ContractAgreementMessageBuilder implements Builder<ContractAgreementMessage> {

    private ContractAgreementMessageImpl contractAgreementMessageImpl;

    public ContractAgreementMessageBuilder() {
        contractAgreementMessageImpl = new ContractAgreementMessageImpl();
    }

    public ContractAgreementMessageBuilder(URI id) {
        this();
        contractAgreementMessageImpl.id = id;
    }

    /**
     * This function allows setting a value for _modelVersion
     *
     * @param _modelVersion_ desired value to be set
     * @return Builder object with new value for _modelVersion
     */
    public ContractAgreementMessageBuilder _modelVersion_(String _modelVersion_) {
        this.contractAgreementMessageImpl.setModelVersion(_modelVersion_);
        return this;
    }

    /**
     * This function allows setting a value for _issued
     *
     * @param _issued_ desired value to be set
     * @return Builder object with new value for _issued
     */
    public ContractAgreementMessageBuilder _issued_(XMLGregorianCalendar _issued_) {
        this.contractAgreementMessageImpl.setIssued(_issued_);
        return this;
    }

    /**
     * This function allows setting a value for _correlationMessage
     *
     * @param _correlationMessage_ desired value to be set
     * @return Builder object with new value for _correlationMessage
     */
    public ContractAgreementMessageBuilder _correlationMessage_(URI _correlationMessage_) {
        this.contractAgreementMessageImpl.setCorrelationMessage(_correlationMessage_);
        return this;
    }

    /**
     * This function allows setting a value for _issuerConnector
     *
     * @param _issuerConnector_ desired value to be set
     * @return Builder object with new value for _issuerConnector
     */
    public ContractAgreementMessageBuilder _issuerConnector_(URI _issuerConnector_) {
        this.contractAgreementMessageImpl.setIssuerConnector(_issuerConnector_);
        return this;
    }

    /**
     * This function allows setting a value for _recipientConnector
     *
     * @param _recipientConnector_ desired value to be set
     * @return Builder object with new value for _recipientConnector
     */
    public ContractAgreementMessageBuilder _recipientConnector_(List<URI> _recipientConnector_) {
        this.contractAgreementMessageImpl.setRecipientConnector(_recipientConnector_);
        return this;
    }

    /**
     * This function allows adding a value to the List _recipientConnector
     *
     * @param _recipientConnector_ desired value to be added
     * @return Builder object with new value for _recipientConnector
     */
    public ContractAgreementMessageBuilder _recipientConnector_(URI _recipientConnector_) {
        this.contractAgreementMessageImpl.getRecipientConnector().add(_recipientConnector_);
        return this;
    }

    /**
     * This function allows setting a value for _senderAgent
     *
     * @param _senderAgent_ desired value to be set
     * @return Builder object with new value for _senderAgent
     */
    public ContractAgreementMessageBuilder _senderAgent_(URI _senderAgent_) {
        this.contractAgreementMessageImpl.setSenderAgent(_senderAgent_);
        return this;
    }

    /**
     * This function allows setting a value for _recipientAgent
     *
     * @param _recipientAgent_ desired value to be set
     * @return Builder object with new value for _recipientAgent
     */
    public ContractAgreementMessageBuilder _recipientAgent_(List<URI> _recipientAgent_) {
        this.contractAgreementMessageImpl.setRecipientAgent(_recipientAgent_);
        return this;
    }

    /**
     * This function allows adding a value to the List _recipientAgent
     *
     * @param _recipientAgent_ desired value to be added
     * @return Builder object with new value for _recipientAgent
     */
    public ContractAgreementMessageBuilder _recipientAgent_(URI _recipientAgent_) {
        this.contractAgreementMessageImpl.getRecipientAgent().add(_recipientAgent_);
        return this;
    }

    /**
     * This function allows setting a value for _securityToken
     *
     * @param _securityToken_ desired value to be set
     * @return Builder object with new value for _securityToken
     */
    public ContractAgreementMessageBuilder _securityToken_(DynamicAttributeToken _securityToken_) {
        this.contractAgreementMessageImpl.setSecurityToken(_securityToken_);
        return this;
    }

    /**
     * This function allows setting a value for _authorizationToken
     *
     * @param _authorizationToken_ desired value to be set
     * @return Builder object with new value for _authorizationToken
     */
    public ContractAgreementMessageBuilder _authorizationToken_(Token _authorizationToken_) {
        this.contractAgreementMessageImpl.setAuthorizationToken(_authorizationToken_);
        return this;
    }

    /**
     * This function allows setting a value for _transferContract
     *
     * @param _transferContract_ desired value to be set
     * @return Builder object with new value for _transferContract
     */
    public ContractAgreementMessageBuilder _transferContract_(URI _transferContract_) {
        this.contractAgreementMessageImpl.setTransferContract(_transferContract_);
        return this;
    }

    /**
     * This function allows setting a value for _contentVersion
     *
     * @param _contentVersion_ desired value to be set
     * @return Builder object with new value for _contentVersion
     */
    public ContractAgreementMessageBuilder _contentVersion_(String _contentVersion_) {
        this.contractAgreementMessageImpl.setContentVersion(_contentVersion_);
        return this;
    }

    /**
     * This function takes the values that were set previously via the other functions of this class and
     * turns them into a Java bean.
     *
     * @return Bean with specified values
     * @throws ConstraintViolationException This exception is thrown, if a validator is used and a
     *         violation is found.
     */
    @Override
    public ContractAgreementMessage build() throws ConstraintViolationException {
        VocabUtil.getInstance().validate(contractAgreementMessageImpl);
        return contractAgreementMessageImpl;
    }
}
