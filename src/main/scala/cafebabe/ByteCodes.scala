package cafebabe

object ByteCodes {

  import AbstractByteCodes._
  import ClassFileTypes._

  private implicit def intToOptionInt(i: Int) = Some(i)

  sealed abstract class ByteCode(val code: U1, se: Option[Int], l: Option[Int]) extends AbstractByteCode {
    val size: Int = 1
    val stackEffect: Option[Int] = se
    val length: Option[Int] = l

    override def toStream(bs: ByteStream): ByteStream = bs << code
  }

  case object AALOAD extends ByteCode(0x32, -1, 1)

  case object AASTORE extends ByteCode(0x53, -3, 1)

  case object ACONST_NULL extends ByteCode(0x1, 1, 1)

  case object ALOAD_0 extends ByteCode(0x2A, 1, 1)

  case object ALOAD_1 extends ByteCode(0x2B, 1, 1)

  case object ALOAD_2 extends ByteCode(0x2C, 1, 1)

  case object ALOAD_3 extends ByteCode(0x2D, 1, 1)

  case object ALOAD extends ByteCode(0x19, 1, 2)

  case object ANEWARRAY extends ByteCode(0xBD, 0, 3)

  case object ARETURN extends ByteCode(0xB0, -1, 1)

  case object ARRAYLENGTH extends ByteCode(0xBE, 0, 1)

  case object ASTORE_0 extends ByteCode(0x4B, -1, 1)

  case object ASTORE_1 extends ByteCode(0x4C, -1, 1)

  case object ASTORE_2 extends ByteCode(0x4D, -1, 1)

  case object ASTORE_3 extends ByteCode(0x4E, -1, 1)

  case object ASTORE extends ByteCode(0x3A, -1, 2)

  case object ATHROW extends ByteCode(0xBF, -1, 1)

  case object BALOAD extends ByteCode(0x33, -1, 1)

  case object BASTORE extends ByteCode(0x54, -3, 1)

  case object BIPUSH extends ByteCode(0x10, 1, 2)

  case object CALOAD extends ByteCode(0x34, -1, 1)

  case object CASTORE extends ByteCode(0x55, -3, 1)

  case object CHECKCAST extends ByteCode(0xC0, 0, 3)

  case object D2F extends ByteCode(0x90, -1, 1)

  case object D2I extends ByteCode(0x8E, -1, 1)

  case object D2L extends ByteCode(0x8F, 0, 1)

  case object DADD extends ByteCode(0x63, -2, 1)

  case object DALOAD extends ByteCode(0x31, 0, 1)

  case object DASTORE extends ByteCode(0x52, -4, 1)

  case object DCMPG extends ByteCode(0x98, -3, 1)

  case object DCMPL extends ByteCode(0x97, -3, 1)

  case object DCONST_0 extends ByteCode(0xE, 2, 1)

  case object DCONST_1 extends ByteCode(0xF, 2, 1)

  case object DDIV extends ByteCode(0x6F, -2, 1)

  case object DLOAD_0 extends ByteCode(0x26, 2, 1)

  case object DLOAD_1 extends ByteCode(0x27, 2, 1)

  case object DLOAD_2 extends ByteCode(0x28, 2, 1)

  case object DLOAD_3 extends ByteCode(0x29, 2, 1)

  case object DLOAD extends ByteCode(0x18, 2, 2)

  case object DMUL extends ByteCode(0x6B, -2, 1)

  case object DNEG extends ByteCode(0x77, 0, 1)

  case object DREM extends ByteCode(0x73, -2, 1)

  case object DRETURN extends ByteCode(0xAF, -2, 1)

  case object DSTORE_0 extends ByteCode(0x47, -2, 1)

  case object DSTORE_1 extends ByteCode(0x48, -2, 1)

  case object DSTORE_2 extends ByteCode(0x49, -2, 1)

  case object DSTORE_3 extends ByteCode(0x4A, -2, 1)

  case object DSTORE extends ByteCode(0x39, -2, 2)

  case object DSUB extends ByteCode(0x67, -2, 1)

  case object DUP2 extends ByteCode(0x5C, 2, 1)

  case object DUP2_X1 extends ByteCode(0x5D, 2, 1)

  case object DUP2_X2 extends ByteCode(0x5E, 2, 1)

  case object DUP extends ByteCode(0x59, 1, 1)

  case object DUP_X1 extends ByteCode(0x5A, 1, 1)

  case object DUP_X2 extends ByteCode(0x5B, 1, 1)

  case object F2D extends ByteCode(0x8D, 1, 1)

  case object F2I extends ByteCode(0x8B, 0, 1)

  case object F2L extends ByteCode(0x8C, 1, 1)

  case object FADD extends ByteCode(0x62, -1, 1)

  case object FALOAD extends ByteCode(0x30, -1, 1)

  case object FASTORE extends ByteCode(0x51, -3, 1)

  case object FCMPG extends ByteCode(0x96, -1, 1)

  case object FCMPL extends ByteCode(0x95, -1, 1)

  case object FCONST_0 extends ByteCode(0xB, 1, 1)

  case object FCONST_1 extends ByteCode(0xC, 1, 1)

  case object FCONST_2 extends ByteCode(0xD, 1, 1)

  case object FDIV extends ByteCode(0x6E, -1, 1)

  case object FLOAD_0 extends ByteCode(0x22, 1, 1)

  case object FLOAD_1 extends ByteCode(0x23, 1, 1)

  case object FLOAD_2 extends ByteCode(0x24, 1, 1)

  case object FLOAD_3 extends ByteCode(0x25, 1, 1)

  case object FLOAD extends ByteCode(0x17, 1, 2)

  case object FMUL extends ByteCode(0x6A, -1, 1)

  case object FNEG extends ByteCode(0x76, 0, 1)

  case object FREM extends ByteCode(0x72, -1, 1)

  case object FRETURN extends ByteCode(0xAE, -1, 1)

  case object FSTORE_0 extends ByteCode(0x43, -1, 1)

  case object FSTORE_1 extends ByteCode(0x44, -1, 1)

  case object FSTORE_2 extends ByteCode(0x45, -1, 1)

  case object FSTORE_3 extends ByteCode(0x46, -1, 1)

  case object FSTORE extends ByteCode(0x38, -1, 2)

  case object FSUB extends ByteCode(0x66, -1, 1)

  case object GETFIELD extends ByteCode(0xB4, None, 3)

  case object GETSTATIC extends ByteCode(0xB2, None, 3)

  case object GOTO extends ByteCode(0xA7, 0, 3)

  case object GOTO_W extends ByteCode(0xC8, 0, 5)

  case object I2B extends ByteCode(0x91, 0, 1)

  case object I2C extends ByteCode(0x92, 0, 1)

  case object I2D extends ByteCode(0x87, 1, 1)

  case object I2F extends ByteCode(0x86, 0, 1)

  case object I2L extends ByteCode(0x85, 1, 1)

  case object I2S extends ByteCode(0x93, 0, 1)

  case object IADD extends ByteCode(0x60, -1, 1)

  case object IALOAD extends ByteCode(0x2E, -1, 1)

  case object IAND extends ByteCode(0x7E, -1, 1)

  case object IASTORE extends ByteCode(0x4F, -3, 1)

  case object ICONST_0 extends ByteCode(0x3, 1, 1)

  case object ICONST_1 extends ByteCode(0x4, 1, 1)

  case object ICONST_2 extends ByteCode(0x5, 1, 1)

  case object ICONST_3 extends ByteCode(0x6, 1, 1)

  case object ICONST_4 extends ByteCode(0x7, 1, 1)

  case object ICONST_5 extends ByteCode(0x8, 1, 1)

  case object ICONST_M1 extends ByteCode(0x2, 1, 1)

  case object IDIV extends ByteCode(0x6C, -1, 1)

  case object IF_ACMPEQ extends ByteCode(0xA5, -2, 3)

  case object IF_ACMPNE extends ByteCode(0xA6, -2, 3)

  case object IFEQ extends ByteCode(0x99, -1, 3)

  case object IFGE extends ByteCode(0x9C, -1, 3)

  case object IFGT extends ByteCode(0x9D, -1, 3)

  case object IF_ICMPEQ extends ByteCode(0x9F, -2, 3)

  case object IF_ICMPGE extends ByteCode(0xA2, -2, 3)

  case object IF_ICMPGT extends ByteCode(0xA3, -2, 3)

  case object IF_ICMPLE extends ByteCode(0xA4, -2, 3)

  case object IF_ICMPLT extends ByteCode(0xA1, -2, 3)

  case object IF_ICMPNE extends ByteCode(0xA0, -2, 3)

  case object IFLE extends ByteCode(0x9E, -1, 3)

  case object IFLT extends ByteCode(0x9B, -1, 3)

  case object IFNE extends ByteCode(0x9A, -1, 3)

  case object IFNONNULL extends ByteCode(0xC7, -1, 3)

  case object IFNULL extends ByteCode(0xC6, -1, 3)

  case object IINC extends ByteCode(0x84, 0, 3)

  case object ILOAD_0 extends ByteCode(0x1A, 1, 1)

  case object ILOAD_1 extends ByteCode(0x1B, 1, 1)

  case object ILOAD_2 extends ByteCode(0x1C, 1, 1)

  case object ILOAD_3 extends ByteCode(0x1D, 1, 1)

  case object ILOAD extends ByteCode(0x15, 1, 2)

  case object IMUL extends ByteCode(0x68, -1, 1)

  case object INEG extends ByteCode(0x74, 0, 1)

  case object INSTANCEOF extends ByteCode(0xC1, 0, 3)

  case object INVOKEINTERFACE extends ByteCode(0xB9, None, 5)

  case object INVOKESPECIAL extends ByteCode(0xB7, None, 3)

  case object INVOKESTATIC extends ByteCode(0xB8, None, 3)

  case object INVOKEVIRTUAL extends ByteCode(0xB6, None, 3)

  case object IOR extends ByteCode(0x80, -1, 1)

  case object IREM extends ByteCode(0x70, -1, 1)

  case object IRETURN extends ByteCode(0xAC, -1, 1)

  case object ISHL extends ByteCode(0x78, -1, 1)

  case object ISHR extends ByteCode(0x7A, -1, 1)

  case object ISTORE_0 extends ByteCode(0x3B, -1, 1)

  case object ISTORE_1 extends ByteCode(0x3C, -1, 1)

  case object ISTORE_2 extends ByteCode(0x3D, -1, 1)

  case object ISTORE_3 extends ByteCode(0x3E, -1, 1)

  case object ISTORE extends ByteCode(0x36, -1, 2)

  case object ISUB extends ByteCode(0x64, -1, 1)

  case object IUSHR extends ByteCode(0x7C, -1, 1)

  case object IXOR extends ByteCode(0x82, -1, 1)

  case object JSR extends ByteCode(0xA8, 1, 3)

  case object JSR_W extends ByteCode(0xC9, 1, 5)

  case object L2D extends ByteCode(0x8A, 0, 1)

  case object L2F extends ByteCode(0x89, -1, 1)

  case object L2I extends ByteCode(0x88, -1, 1)

  case object LADD extends ByteCode(0x61, -2, 1)

  case object LALOAD extends ByteCode(0x2F, 0, 1)

  case object LAND extends ByteCode(0x7F, -2, 1)

  case object LASTORE extends ByteCode(0x50, -4, 1)

  case object LCMP extends ByteCode(0x94, -3, 1)

  case object LCONST_0 extends ByteCode(0x9, 2, 1)

  case object LCONST_1 extends ByteCode(0xA, 2, 1)

  case object LDC2_W extends ByteCode(0x14, 2, 3)

  case object LDC extends ByteCode(0x12, 1, 2)

  case object LDC_W extends ByteCode(0x13, 1, 3)

  case object LDIV extends ByteCode(0x6D, -2, 1)

  case object LLOAD_0 extends ByteCode(0x1E, 2, 1)

  case object LLOAD_1 extends ByteCode(0x1F, 2, 1)

  case object LLOAD_2 extends ByteCode(0x20, 2, 1)

  case object LLOAD_3 extends ByteCode(0x21, 2, 1)

  case object LLOAD extends ByteCode(0x16, 2, 2)

  case object LMUL extends ByteCode(0x69, -2, 1)

  case object LNEG extends ByteCode(0x75, 0, 1)

  case object LOOKUPSWITCH extends ByteCode(0xAB, -1, None)

  case object LOR extends ByteCode(0x81, -2, 1)

  case object LREM extends ByteCode(0x71, -2, 1)

  case object LRETURN extends ByteCode(0xAD, -2, 1)

  case object LSHL extends ByteCode(0x79, -1, 1)

  case object LSHR extends ByteCode(0x7B, -1, 1)

  case object LSTORE_0 extends ByteCode(0x3F, -2, 1)

  case object LSTORE_1 extends ByteCode(0x40, -2, 1)

  case object LSTORE_2 extends ByteCode(0x41, -2, 1)

  case object LSTORE_3 extends ByteCode(0x42, -2, 1)

  case object LSTORE extends ByteCode(0x37, -2, 2)

  case object LSUB extends ByteCode(0x65, -2, 1)

  case object LUSHR extends ByteCode(0x7D, -1, 1)

  case object LXOR extends ByteCode(0x83, -2, 1)

  case object MONITORENTER extends ByteCode(0xC2, -1, 1)

  case object MONITOREXIT extends ByteCode(0xC3, -1, 1)

  case object MULTIANEWARRAY extends ByteCode(0xC5, None, 4)

  case object NEWARRAY extends ByteCode(0xBC, 0, 2)

  case object NEW extends ByteCode(0xBB, 1, 3)

  case object NOP extends ByteCode(0x0, 0, 1)

  case object POP2 extends ByteCode(0x58, -2, 1)

  case object POP extends ByteCode(0x57, -1, 1)

  case object PUTFIELD extends ByteCode(0xB5, None, 3)

  case object PUTSTATIC extends ByteCode(0xB3, None, 3)

  case object RET extends ByteCode(0xA9, 0, 2)

  case object RETURN extends ByteCode(0xB1, 0, 1)

  case object SALOAD extends ByteCode(0x35, -1, 1)

  case object SASTORE extends ByteCode(0x56, -3, 1)

  case object SIPUSH extends ByteCode(0x11, 1, 3)

  case object SWAP extends ByteCode(0x5F, 0, 1)

  case object TABLESWITCH extends ByteCode(0xAA, -1, None)

  case object WIDE extends ByteCode(0xC4, None, None)

}
