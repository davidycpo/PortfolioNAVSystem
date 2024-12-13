// Generated by the protocol buffer compiler. DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: src/main/proto/PriceChange.proto
// Protobuf Java Version: 4.29.1

package model;

public final class PriceChangeOuterClass {
	private PriceChangeOuterClass() {
	}

	static {
		com.google.protobuf.RuntimeVersion.validateProtobufGencodeVersion(
				com.google.protobuf.RuntimeVersion.RuntimeDomain.PUBLIC,
				/* major= */ 4, /* minor= */ 29, /* patch= */ 1, /* suffix= */ "",
				PriceChangeOuterClass.class.getName());
	}

	public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {
	}

	public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
		registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
	}

	public interface PriceChangeOrBuilder extends
			// @@protoc_insertion_point(interface_extends:model.PriceChange)
			com.google.protobuf.MessageOrBuilder {

		/**
		 * <code>string ticker = 1;</code>
		 * 
		 * @return The ticker.
		 */
		java.lang.String getTicker();

		/**
		 * <code>string ticker = 1;</code>
		 * 
		 * @return The bytes for ticker.
		 */
		com.google.protobuf.ByteString getTickerBytes();

		/**
		 * <code>double price = 2;</code>
		 * 
		 * @return The price.
		 */
		double getPrice();
	}

	/**
	 * Protobuf type {@code model.PriceChange}
	 */
	public static final class PriceChange extends com.google.protobuf.GeneratedMessage implements
			// @@protoc_insertion_point(message_implements:model.PriceChange)
			PriceChangeOrBuilder {
		private static final long serialVersionUID = 0L;
		static {
			com.google.protobuf.RuntimeVersion.validateProtobufGencodeVersion(
					com.google.protobuf.RuntimeVersion.RuntimeDomain.PUBLIC,
					/* major= */ 4, /* minor= */ 29, /* patch= */ 1,
					/* suffix= */ "", PriceChange.class.getName());
		}

		// Use PriceChange.newBuilder() to construct.
		private PriceChange(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
			super(builder);
		}

		private PriceChange() {
			ticker_ = "";
		}

		public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
			return model.PriceChangeOuterClass.internal_static_model_PriceChange_descriptor;
		}

		@java.lang.Override
		protected com.google.protobuf.GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
			return model.PriceChangeOuterClass.internal_static_model_PriceChange_fieldAccessorTable
					.ensureFieldAccessorsInitialized(model.PriceChangeOuterClass.PriceChange.class,
							model.PriceChangeOuterClass.PriceChange.Builder.class);
		}

		public static final int TICKER_FIELD_NUMBER = 1;
		@SuppressWarnings("serial")
		private volatile java.lang.Object ticker_ = "";

		/**
		 * <code>string ticker = 1;</code>
		 * 
		 * @return The ticker.
		 */
		@java.lang.Override
		public java.lang.String getTicker() {
			java.lang.Object ref = ticker_;
			if (ref instanceof java.lang.String) {
				return (java.lang.String) ref;
			} else {
				com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
				java.lang.String s = bs.toStringUtf8();
				ticker_ = s;
				return s;
			}
		}

		/**
		 * <code>string ticker = 1;</code>
		 * 
		 * @return The bytes for ticker.
		 */
		@java.lang.Override
		public com.google.protobuf.ByteString getTickerBytes() {
			java.lang.Object ref = ticker_;
			if (ref instanceof java.lang.String) {
				com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
				ticker_ = b;
				return b;
			} else {
				return (com.google.protobuf.ByteString) ref;
			}
		}

		public static final int PRICE_FIELD_NUMBER = 2;
		private double price_ = 0D;

		/**
		 * <code>double price = 2;</code>
		 * 
		 * @return The price.
		 */
		@java.lang.Override
		public double getPrice() {
			return price_;
		}

		private byte memoizedIsInitialized = -1;

		@java.lang.Override
		public final boolean isInitialized() {
			byte isInitialized = memoizedIsInitialized;
			if (isInitialized == 1)
				return true;
			if (isInitialized == 0)
				return false;

			memoizedIsInitialized = 1;
			return true;
		}

		@java.lang.Override
		public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
			if (!com.google.protobuf.GeneratedMessage.isStringEmpty(ticker_)) {
				com.google.protobuf.GeneratedMessage.writeString(output, 1, ticker_);
			}
			if (java.lang.Double.doubleToRawLongBits(price_) != 0) {
				output.writeDouble(2, price_);
			}
			getUnknownFields().writeTo(output);
		}

		@java.lang.Override
		public int getSerializedSize() {
			int size = memoizedSize;
			if (size != -1)
				return size;

			size = 0;
			if (!com.google.protobuf.GeneratedMessage.isStringEmpty(ticker_)) {
				size += com.google.protobuf.GeneratedMessage.computeStringSize(1, ticker_);
			}
			if (java.lang.Double.doubleToRawLongBits(price_) != 0) {
				size += com.google.protobuf.CodedOutputStream.computeDoubleSize(2, price_);
			}
			size += getUnknownFields().getSerializedSize();
			memoizedSize = size;
			return size;
		}

		@java.lang.Override
		public boolean equals(final java.lang.Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof model.PriceChangeOuterClass.PriceChange)) {
				return super.equals(obj);
			}
			model.PriceChangeOuterClass.PriceChange other = (model.PriceChangeOuterClass.PriceChange) obj;

			if (!getTicker().equals(other.getTicker()))
				return false;
			if (java.lang.Double.doubleToLongBits(getPrice()) != java.lang.Double.doubleToLongBits(other.getPrice()))
				return false;
			if (!getUnknownFields().equals(other.getUnknownFields()))
				return false;
			return true;
		}

		@java.lang.Override
		public int hashCode() {
			if (memoizedHashCode != 0) {
				return memoizedHashCode;
			}
			int hash = 41;
			hash = (19 * hash) + getDescriptor().hashCode();
			hash = (37 * hash) + TICKER_FIELD_NUMBER;
			hash = (53 * hash) + getTicker().hashCode();
			hash = (37 * hash) + PRICE_FIELD_NUMBER;
			hash = (53 * hash) + com.google.protobuf.Internal.hashLong(java.lang.Double.doubleToLongBits(getPrice()));
			hash = (29 * hash) + getUnknownFields().hashCode();
			memoizedHashCode = hash;
			return hash;
		}

		public static model.PriceChangeOuterClass.PriceChange parseFrom(java.nio.ByteBuffer data)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data);
		}

		public static model.PriceChangeOuterClass.PriceChange parseFrom(java.nio.ByteBuffer data,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data, extensionRegistry);
		}

		public static model.PriceChangeOuterClass.PriceChange parseFrom(com.google.protobuf.ByteString data)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data);
		}

		public static model.PriceChangeOuterClass.PriceChange parseFrom(com.google.protobuf.ByteString data,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data, extensionRegistry);
		}

		public static model.PriceChangeOuterClass.PriceChange parseFrom(byte[] data)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data);
		}

		public static model.PriceChangeOuterClass.PriceChange parseFrom(byte[] data,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data, extensionRegistry);
		}

		public static model.PriceChangeOuterClass.PriceChange parseFrom(java.io.InputStream input)
				throws java.io.IOException {
			return com.google.protobuf.GeneratedMessage.parseWithIOException(PARSER, input);
		}

		public static model.PriceChangeOuterClass.PriceChange parseFrom(java.io.InputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
		}

		public static model.PriceChangeOuterClass.PriceChange parseDelimitedFrom(java.io.InputStream input)
				throws java.io.IOException {
			return com.google.protobuf.GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
		}

		public static model.PriceChangeOuterClass.PriceChange parseDelimitedFrom(java.io.InputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
		}

		public static model.PriceChangeOuterClass.PriceChange parseFrom(com.google.protobuf.CodedInputStream input)
				throws java.io.IOException {
			return com.google.protobuf.GeneratedMessage.parseWithIOException(PARSER, input);
		}

		public static model.PriceChangeOuterClass.PriceChange parseFrom(com.google.protobuf.CodedInputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
		}

		@java.lang.Override
		public Builder newBuilderForType() {
			return newBuilder();
		}

		public static Builder newBuilder() {
			return DEFAULT_INSTANCE.toBuilder();
		}

		public static Builder newBuilder(model.PriceChangeOuterClass.PriceChange prototype) {
			return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
		}

		@java.lang.Override
		public Builder toBuilder() {
			return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
		}

		@java.lang.Override
		protected Builder newBuilderForType(com.google.protobuf.GeneratedMessage.BuilderParent parent) {
			Builder builder = new Builder(parent);
			return builder;
		}

		/**
		 * Protobuf type {@code model.PriceChange}
		 */
		public static final class Builder extends com.google.protobuf.GeneratedMessage.Builder<Builder> implements
				// @@protoc_insertion_point(builder_implements:model.PriceChange)
				model.PriceChangeOuterClass.PriceChangeOrBuilder {
			public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
				return model.PriceChangeOuterClass.internal_static_model_PriceChange_descriptor;
			}

			@java.lang.Override
			protected com.google.protobuf.GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
				return model.PriceChangeOuterClass.internal_static_model_PriceChange_fieldAccessorTable
						.ensureFieldAccessorsInitialized(model.PriceChangeOuterClass.PriceChange.class,
								model.PriceChangeOuterClass.PriceChange.Builder.class);
			}

			// Construct using
			// model.PriceChangeOuterClass.PriceChange.newBuilder()
			private Builder() {

			}

			private Builder(com.google.protobuf.GeneratedMessage.BuilderParent parent) {
				super(parent);

			}

			@java.lang.Override
			public Builder clear() {
				super.clear();
				bitField0_ = 0;
				ticker_ = "";
				price_ = 0D;
				return this;
			}

			@java.lang.Override
			public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
				return model.PriceChangeOuterClass.internal_static_model_PriceChange_descriptor;
			}

			@java.lang.Override
			public model.PriceChangeOuterClass.PriceChange getDefaultInstanceForType() {
				return model.PriceChangeOuterClass.PriceChange.getDefaultInstance();
			}

			@java.lang.Override
			public model.PriceChangeOuterClass.PriceChange build() {
				model.PriceChangeOuterClass.PriceChange result = buildPartial();
				if (!result.isInitialized()) {
					throw newUninitializedMessageException(result);
				}
				return result;
			}

			@java.lang.Override
			public model.PriceChangeOuterClass.PriceChange buildPartial() {
				model.PriceChangeOuterClass.PriceChange result = new model.PriceChangeOuterClass.PriceChange(this);
				if (bitField0_ != 0) {
					buildPartial0(result);
				}
				onBuilt();
				return result;
			}

			private void buildPartial0(model.PriceChangeOuterClass.PriceChange result) {
				int from_bitField0_ = bitField0_;
				if (((from_bitField0_ & 0x00000001) != 0)) {
					result.ticker_ = ticker_;
				}
				if (((from_bitField0_ & 0x00000002) != 0)) {
					result.price_ = price_;
				}
			}

			@java.lang.Override
			public Builder mergeFrom(com.google.protobuf.Message other) {
				if (other instanceof model.PriceChangeOuterClass.PriceChange) {
					return mergeFrom((model.PriceChangeOuterClass.PriceChange) other);
				} else {
					super.mergeFrom(other);
					return this;
				}
			}

			public Builder mergeFrom(model.PriceChangeOuterClass.PriceChange other) {
				if (other == model.PriceChangeOuterClass.PriceChange.getDefaultInstance())
					return this;
				if (!other.getTicker().isEmpty()) {
					ticker_ = other.ticker_;
					bitField0_ |= 0x00000001;
					onChanged();
				}
				if (other.getPrice() != 0D) {
					setPrice(other.getPrice());
				}
				this.mergeUnknownFields(other.getUnknownFields());
				onChanged();
				return this;
			}

			@java.lang.Override
			public final boolean isInitialized() {
				return true;
			}

			@java.lang.Override
			public Builder mergeFrom(com.google.protobuf.CodedInputStream input,
					com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
				if (extensionRegistry == null) {
					throw new java.lang.NullPointerException();
				}
				try {
					boolean done = false;
					while (!done) {
						int tag = input.readTag();
						switch (tag) {
						case 0:
							done = true;
							break;
						case 10: {
							ticker_ = input.readStringRequireUtf8();
							bitField0_ |= 0x00000001;
							break;
						} // case 10
						case 17: {
							price_ = input.readDouble();
							bitField0_ |= 0x00000002;
							break;
						} // case 17
						default: {
							if (!super.parseUnknownField(input, extensionRegistry, tag)) {
								done = true; // was an endgroup tag
							}
							break;
						} // default:
						} // switch (tag)
					} // while (!done)
				} catch (com.google.protobuf.InvalidProtocolBufferException e) {
					throw e.unwrapIOException();
				} finally {
					onChanged();
				} // finally
				return this;
			}

			private int bitField0_;

			private java.lang.Object ticker_ = "";

			/**
			 * <code>string ticker = 1;</code>
			 * 
			 * @return The ticker.
			 */
			public java.lang.String getTicker() {
				java.lang.Object ref = ticker_;
				if (!(ref instanceof java.lang.String)) {
					com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
					java.lang.String s = bs.toStringUtf8();
					ticker_ = s;
					return s;
				} else {
					return (java.lang.String) ref;
				}
			}

			/**
			 * <code>string ticker = 1;</code>
			 * 
			 * @return The bytes for ticker.
			 */
			public com.google.protobuf.ByteString getTickerBytes() {
				java.lang.Object ref = ticker_;
				if (ref instanceof String) {
					com.google.protobuf.ByteString b = com.google.protobuf.ByteString
							.copyFromUtf8((java.lang.String) ref);
					ticker_ = b;
					return b;
				} else {
					return (com.google.protobuf.ByteString) ref;
				}
			}

			/**
			 * <code>string ticker = 1;</code>
			 * 
			 * @param value
			 *            The ticker to set.
			 * @return This builder for chaining.
			 */
			public Builder setTicker(java.lang.String value) {
				if (value == null) {
					throw new NullPointerException();
				}
				ticker_ = value;
				bitField0_ |= 0x00000001;
				onChanged();
				return this;
			}

			/**
			 * <code>string ticker = 1;</code>
			 * 
			 * @return This builder for chaining.
			 */
			public Builder clearTicker() {
				ticker_ = getDefaultInstance().getTicker();
				bitField0_ = (bitField0_ & ~0x00000001);
				onChanged();
				return this;
			}

			/**
			 * <code>string ticker = 1;</code>
			 * 
			 * @param value
			 *            The bytes for ticker to set.
			 * @return This builder for chaining.
			 */
			public Builder setTickerBytes(com.google.protobuf.ByteString value) {
				if (value == null) {
					throw new NullPointerException();
				}
				checkByteStringIsUtf8(value);
				ticker_ = value;
				bitField0_ |= 0x00000001;
				onChanged();
				return this;
			}

			private double price_;

			/**
			 * <code>double price = 2;</code>
			 * 
			 * @return The price.
			 */
			@java.lang.Override
			public double getPrice() {
				return price_;
			}

			/**
			 * <code>double price = 2;</code>
			 * 
			 * @param value
			 *            The price to set.
			 * @return This builder for chaining.
			 */
			public Builder setPrice(double value) {

				price_ = value;
				bitField0_ |= 0x00000002;
				onChanged();
				return this;
			}

			/**
			 * <code>double price = 2;</code>
			 * 
			 * @return This builder for chaining.
			 */
			public Builder clearPrice() {
				bitField0_ = (bitField0_ & ~0x00000002);
				price_ = 0D;
				onChanged();
				return this;
			}

			// @@protoc_insertion_point(builder_scope:model.PriceChange)
		}

		// @@protoc_insertion_point(class_scope:model.PriceChange)
		private static final model.PriceChangeOuterClass.PriceChange DEFAULT_INSTANCE;
		static {
			DEFAULT_INSTANCE = new model.PriceChangeOuterClass.PriceChange();
		}

		public static model.PriceChangeOuterClass.PriceChange getDefaultInstance() {
			return DEFAULT_INSTANCE;
		}

		private static final com.google.protobuf.Parser<PriceChange> PARSER = new com.google.protobuf.AbstractParser<PriceChange>() {
			@java.lang.Override
			public PriceChange parsePartialFrom(com.google.protobuf.CodedInputStream input,
					com.google.protobuf.ExtensionRegistryLite extensionRegistry)
					throws com.google.protobuf.InvalidProtocolBufferException {
				Builder builder = newBuilder();
				try {
					builder.mergeFrom(input, extensionRegistry);
				} catch (com.google.protobuf.InvalidProtocolBufferException e) {
					throw e.setUnfinishedMessage(builder.buildPartial());
				} catch (com.google.protobuf.UninitializedMessageException e) {
					throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
				} catch (java.io.IOException e) {
					throw new com.google.protobuf.InvalidProtocolBufferException(e)
							.setUnfinishedMessage(builder.buildPartial());
				}
				return builder.buildPartial();
			}
		};

		public static com.google.protobuf.Parser<PriceChange> parser() {
			return PARSER;
		}

		@java.lang.Override
		public com.google.protobuf.Parser<PriceChange> getParserForType() {
			return PARSER;
		}

		@java.lang.Override
		public model.PriceChangeOuterClass.PriceChange getDefaultInstanceForType() {
			return DEFAULT_INSTANCE;
		}

	}

	private static final com.google.protobuf.Descriptors.Descriptor internal_static_model_PriceChange_descriptor;
	private static final com.google.protobuf.GeneratedMessage.FieldAccessorTable internal_static_model_PriceChange_fieldAccessorTable;

	public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
		return descriptor;
	}

	private static com.google.protobuf.Descriptors.FileDescriptor descriptor;
	static {
		java.lang.String[] descriptorData = { "\n src/main/proto/PriceChange.proto\022\005mode"
				+ "l\",\n\013PriceChange\022\016\n\006ticker\030\001 \001(\t\022\r\n\005pric"
				+ "e\030\002 \001(\001b\006proto3" };
		descriptor = com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData,
				new com.google.protobuf.Descriptors.FileDescriptor[] {});
		internal_static_model_PriceChange_descriptor = getDescriptor().getMessageTypes().get(0);
		internal_static_model_PriceChange_fieldAccessorTable = new com.google.protobuf.GeneratedMessage.FieldAccessorTable(
				internal_static_model_PriceChange_descriptor, new java.lang.String[] { "Ticker", "Price", });
		descriptor.resolveAllFeaturesImmutable();
	}

	// @@protoc_insertion_point(outer_class_scope)
}
