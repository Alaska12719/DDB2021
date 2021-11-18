// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ddb.proto

package network.proto;

/**
 * Protobuf type {@code SaveTableRequest}
 */
public final class SaveTableRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:SaveTableRequest)
    SaveTableRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use SaveTableRequest.newBuilder() to construct.
  private SaveTableRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private SaveTableRequest() {
    tableName_ = "";
    attributeMeta_ = "";
    attributeValues_ = com.google.protobuf.LazyStringArrayList.EMPTY;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new SaveTableRequest();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private SaveTableRequest(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();
            bitField0_ |= 0x00000001;
            tableName_ = s;
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();
            bitField0_ |= 0x00000002;
            attributeMeta_ = s;
            break;
          }
          case 26: {
            java.lang.String s = input.readStringRequireUtf8();
            if (!((mutable_bitField0_ & 0x00000004) != 0)) {
              attributeValues_ = new com.google.protobuf.LazyStringArrayList();
              mutable_bitField0_ |= 0x00000004;
            }
            attributeValues_.add(s);
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      if (((mutable_bitField0_ & 0x00000004) != 0)) {
        attributeValues_ = attributeValues_.getUnmodifiableView();
      }
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return network.proto.DdbProto.internal_static_SaveTableRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return network.proto.DdbProto.internal_static_SaveTableRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            network.proto.SaveTableRequest.class, network.proto.SaveTableRequest.Builder.class);
  }

  private int bitField0_;
  public static final int TABLENAME_FIELD_NUMBER = 1;
  private volatile java.lang.Object tableName_;
  /**
   * <code>optional string tableName = 1;</code>
   * @return Whether the tableName field is set.
   */
  @java.lang.Override
  public boolean hasTableName() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   * <code>optional string tableName = 1;</code>
   * @return The tableName.
   */
  @java.lang.Override
  public java.lang.String getTableName() {
    java.lang.Object ref = tableName_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      tableName_ = s;
      return s;
    }
  }
  /**
   * <code>optional string tableName = 1;</code>
   * @return The bytes for tableName.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getTableNameBytes() {
    java.lang.Object ref = tableName_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      tableName_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int ATTRIBUTEMETA_FIELD_NUMBER = 2;
  private volatile java.lang.Object attributeMeta_;
  /**
   * <code>optional string attributeMeta = 2;</code>
   * @return Whether the attributeMeta field is set.
   */
  @java.lang.Override
  public boolean hasAttributeMeta() {
    return ((bitField0_ & 0x00000002) != 0);
  }
  /**
   * <code>optional string attributeMeta = 2;</code>
   * @return The attributeMeta.
   */
  @java.lang.Override
  public java.lang.String getAttributeMeta() {
    java.lang.Object ref = attributeMeta_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      attributeMeta_ = s;
      return s;
    }
  }
  /**
   * <code>optional string attributeMeta = 2;</code>
   * @return The bytes for attributeMeta.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getAttributeMetaBytes() {
    java.lang.Object ref = attributeMeta_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      attributeMeta_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int ATTRIBUTEVALUES_FIELD_NUMBER = 3;
  private com.google.protobuf.LazyStringList attributeValues_;
  /**
   * <code>repeated string attributeValues = 3;</code>
   * @return A list containing the attributeValues.
   */
  public com.google.protobuf.ProtocolStringList
      getAttributeValuesList() {
    return attributeValues_;
  }
  /**
   * <code>repeated string attributeValues = 3;</code>
   * @return The count of attributeValues.
   */
  public int getAttributeValuesCount() {
    return attributeValues_.size();
  }
  /**
   * <code>repeated string attributeValues = 3;</code>
   * @param index The index of the element to return.
   * @return The attributeValues at the given index.
   */
  public java.lang.String getAttributeValues(int index) {
    return attributeValues_.get(index);
  }
  /**
   * <code>repeated string attributeValues = 3;</code>
   * @param index The index of the value to return.
   * @return The bytes of the attributeValues at the given index.
   */
  public com.google.protobuf.ByteString
      getAttributeValuesBytes(int index) {
    return attributeValues_.getByteString(index);
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (((bitField0_ & 0x00000001) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, tableName_);
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, attributeMeta_);
    }
    for (int i = 0; i < attributeValues_.size(); i++) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, attributeValues_.getRaw(i));
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (((bitField0_ & 0x00000001) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, tableName_);
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, attributeMeta_);
    }
    {
      int dataSize = 0;
      for (int i = 0; i < attributeValues_.size(); i++) {
        dataSize += computeStringSizeNoTag(attributeValues_.getRaw(i));
      }
      size += dataSize;
      size += 1 * getAttributeValuesList().size();
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof network.proto.SaveTableRequest)) {
      return super.equals(obj);
    }
    network.proto.SaveTableRequest other = (network.proto.SaveTableRequest) obj;

    if (hasTableName() != other.hasTableName()) return false;
    if (hasTableName()) {
      if (!getTableName()
          .equals(other.getTableName())) return false;
    }
    if (hasAttributeMeta() != other.hasAttributeMeta()) return false;
    if (hasAttributeMeta()) {
      if (!getAttributeMeta()
          .equals(other.getAttributeMeta())) return false;
    }
    if (!getAttributeValuesList()
        .equals(other.getAttributeValuesList())) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (hasTableName()) {
      hash = (37 * hash) + TABLENAME_FIELD_NUMBER;
      hash = (53 * hash) + getTableName().hashCode();
    }
    if (hasAttributeMeta()) {
      hash = (37 * hash) + ATTRIBUTEMETA_FIELD_NUMBER;
      hash = (53 * hash) + getAttributeMeta().hashCode();
    }
    if (getAttributeValuesCount() > 0) {
      hash = (37 * hash) + ATTRIBUTEVALUES_FIELD_NUMBER;
      hash = (53 * hash) + getAttributeValuesList().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static network.proto.SaveTableRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static network.proto.SaveTableRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static network.proto.SaveTableRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static network.proto.SaveTableRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static network.proto.SaveTableRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static network.proto.SaveTableRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static network.proto.SaveTableRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static network.proto.SaveTableRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static network.proto.SaveTableRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static network.proto.SaveTableRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static network.proto.SaveTableRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static network.proto.SaveTableRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(network.proto.SaveTableRequest prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code SaveTableRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:SaveTableRequest)
      network.proto.SaveTableRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return network.proto.DdbProto.internal_static_SaveTableRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return network.proto.DdbProto.internal_static_SaveTableRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              network.proto.SaveTableRequest.class, network.proto.SaveTableRequest.Builder.class);
    }

    // Construct using network.proto.SaveTableRequest.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      tableName_ = "";
      bitField0_ = (bitField0_ & ~0x00000001);
      attributeMeta_ = "";
      bitField0_ = (bitField0_ & ~0x00000002);
      attributeValues_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000004);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return network.proto.DdbProto.internal_static_SaveTableRequest_descriptor;
    }

    @java.lang.Override
    public network.proto.SaveTableRequest getDefaultInstanceForType() {
      return network.proto.SaveTableRequest.getDefaultInstance();
    }

    @java.lang.Override
    public network.proto.SaveTableRequest build() {
      network.proto.SaveTableRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public network.proto.SaveTableRequest buildPartial() {
      network.proto.SaveTableRequest result = new network.proto.SaveTableRequest(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        to_bitField0_ |= 0x00000001;
      }
      result.tableName_ = tableName_;
      if (((from_bitField0_ & 0x00000002) != 0)) {
        to_bitField0_ |= 0x00000002;
      }
      result.attributeMeta_ = attributeMeta_;
      if (((bitField0_ & 0x00000004) != 0)) {
        attributeValues_ = attributeValues_.getUnmodifiableView();
        bitField0_ = (bitField0_ & ~0x00000004);
      }
      result.attributeValues_ = attributeValues_;
      result.bitField0_ = to_bitField0_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof network.proto.SaveTableRequest) {
        return mergeFrom((network.proto.SaveTableRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(network.proto.SaveTableRequest other) {
      if (other == network.proto.SaveTableRequest.getDefaultInstance()) return this;
      if (other.hasTableName()) {
        bitField0_ |= 0x00000001;
        tableName_ = other.tableName_;
        onChanged();
      }
      if (other.hasAttributeMeta()) {
        bitField0_ |= 0x00000002;
        attributeMeta_ = other.attributeMeta_;
        onChanged();
      }
      if (!other.attributeValues_.isEmpty()) {
        if (attributeValues_.isEmpty()) {
          attributeValues_ = other.attributeValues_;
          bitField0_ = (bitField0_ & ~0x00000004);
        } else {
          ensureAttributeValuesIsMutable();
          attributeValues_.addAll(other.attributeValues_);
        }
        onChanged();
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      network.proto.SaveTableRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (network.proto.SaveTableRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private java.lang.Object tableName_ = "";
    /**
     * <code>optional string tableName = 1;</code>
     * @return Whether the tableName field is set.
     */
    public boolean hasTableName() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>optional string tableName = 1;</code>
     * @return The tableName.
     */
    public java.lang.String getTableName() {
      java.lang.Object ref = tableName_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        tableName_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string tableName = 1;</code>
     * @return The bytes for tableName.
     */
    public com.google.protobuf.ByteString
        getTableNameBytes() {
      java.lang.Object ref = tableName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        tableName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string tableName = 1;</code>
     * @param value The tableName to set.
     * @return This builder for chaining.
     */
    public Builder setTableName(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
      tableName_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string tableName = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearTableName() {
      bitField0_ = (bitField0_ & ~0x00000001);
      tableName_ = getDefaultInstance().getTableName();
      onChanged();
      return this;
    }
    /**
     * <code>optional string tableName = 1;</code>
     * @param value The bytes for tableName to set.
     * @return This builder for chaining.
     */
    public Builder setTableNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      bitField0_ |= 0x00000001;
      tableName_ = value;
      onChanged();
      return this;
    }

    private java.lang.Object attributeMeta_ = "";
    /**
     * <code>optional string attributeMeta = 2;</code>
     * @return Whether the attributeMeta field is set.
     */
    public boolean hasAttributeMeta() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>optional string attributeMeta = 2;</code>
     * @return The attributeMeta.
     */
    public java.lang.String getAttributeMeta() {
      java.lang.Object ref = attributeMeta_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        attributeMeta_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string attributeMeta = 2;</code>
     * @return The bytes for attributeMeta.
     */
    public com.google.protobuf.ByteString
        getAttributeMetaBytes() {
      java.lang.Object ref = attributeMeta_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        attributeMeta_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string attributeMeta = 2;</code>
     * @param value The attributeMeta to set.
     * @return This builder for chaining.
     */
    public Builder setAttributeMeta(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
      attributeMeta_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string attributeMeta = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearAttributeMeta() {
      bitField0_ = (bitField0_ & ~0x00000002);
      attributeMeta_ = getDefaultInstance().getAttributeMeta();
      onChanged();
      return this;
    }
    /**
     * <code>optional string attributeMeta = 2;</code>
     * @param value The bytes for attributeMeta to set.
     * @return This builder for chaining.
     */
    public Builder setAttributeMetaBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      bitField0_ |= 0x00000002;
      attributeMeta_ = value;
      onChanged();
      return this;
    }

    private com.google.protobuf.LazyStringList attributeValues_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    private void ensureAttributeValuesIsMutable() {
      if (!((bitField0_ & 0x00000004) != 0)) {
        attributeValues_ = new com.google.protobuf.LazyStringArrayList(attributeValues_);
        bitField0_ |= 0x00000004;
       }
    }
    /**
     * <code>repeated string attributeValues = 3;</code>
     * @return A list containing the attributeValues.
     */
    public com.google.protobuf.ProtocolStringList
        getAttributeValuesList() {
      return attributeValues_.getUnmodifiableView();
    }
    /**
     * <code>repeated string attributeValues = 3;</code>
     * @return The count of attributeValues.
     */
    public int getAttributeValuesCount() {
      return attributeValues_.size();
    }
    /**
     * <code>repeated string attributeValues = 3;</code>
     * @param index The index of the element to return.
     * @return The attributeValues at the given index.
     */
    public java.lang.String getAttributeValues(int index) {
      return attributeValues_.get(index);
    }
    /**
     * <code>repeated string attributeValues = 3;</code>
     * @param index The index of the value to return.
     * @return The bytes of the attributeValues at the given index.
     */
    public com.google.protobuf.ByteString
        getAttributeValuesBytes(int index) {
      return attributeValues_.getByteString(index);
    }
    /**
     * <code>repeated string attributeValues = 3;</code>
     * @param index The index to set the value at.
     * @param value The attributeValues to set.
     * @return This builder for chaining.
     */
    public Builder setAttributeValues(
        int index, java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureAttributeValuesIsMutable();
      attributeValues_.set(index, value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string attributeValues = 3;</code>
     * @param value The attributeValues to add.
     * @return This builder for chaining.
     */
    public Builder addAttributeValues(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureAttributeValuesIsMutable();
      attributeValues_.add(value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string attributeValues = 3;</code>
     * @param values The attributeValues to add.
     * @return This builder for chaining.
     */
    public Builder addAllAttributeValues(
        java.lang.Iterable<java.lang.String> values) {
      ensureAttributeValuesIsMutable();
      com.google.protobuf.AbstractMessageLite.Builder.addAll(
          values, attributeValues_);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string attributeValues = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearAttributeValues() {
      attributeValues_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000004);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string attributeValues = 3;</code>
     * @param value The bytes of the attributeValues to add.
     * @return This builder for chaining.
     */
    public Builder addAttributeValuesBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      ensureAttributeValuesIsMutable();
      attributeValues_.add(value);
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:SaveTableRequest)
  }

  // @@protoc_insertion_point(class_scope:SaveTableRequest)
  private static final network.proto.SaveTableRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new network.proto.SaveTableRequest();
  }

  public static network.proto.SaveTableRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<SaveTableRequest>
      PARSER = new com.google.protobuf.AbstractParser<SaveTableRequest>() {
    @java.lang.Override
    public SaveTableRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new SaveTableRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<SaveTableRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<SaveTableRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public network.proto.SaveTableRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
