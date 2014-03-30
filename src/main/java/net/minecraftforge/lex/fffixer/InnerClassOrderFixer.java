package net.minecraftforge.lex.fffixer;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import java.util.Iterator;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.sun.xml.internal.ws.org.objectweb.asm.Type;


/**
 * Fixes decompiler differences between JVM versions caused by HashSet's sorting order changing between JVM implementations.
 * Simple solution is to hijack the Iterator to make it use a properly sorted one.
 * Thanks to fry for finding this issue with class names, which then led me to look for var names.
 * 
 * Code Injected:
 *   var = net.minecraftfroge.lex.fffixer.Util.sortLvs(var);
 * 
 * @author LexManos
 *
 */
public class InnerClassOrderFixer implements IClassProcessor
{
    private FFFixerImpl inst;
    public InnerClassOrderFixer(FFFixerImpl inst)
    {
        this.inst = inst;
    }

    @Override
    public void process(ClassNode node)
    {
        if (node.name.equals("de"))
        {
            node.interfaces.add("java/lang/Comparable");

            MethodNode mn = new MethodNode(ACC_PUBLIC, "compareTo", "(Lpkg/de;)I", null, null);
            mn.visitCode();
            mn.visitVarInsn(ALOAD, 0);
            mn.visitFieldInsn(GETFIELD, "pkg/de", "a", "I");
            mn.visitVarInsn(ALOAD, 1);
            mn.visitFieldInsn(GETFIELD, "pkg/de", "a", "I");
            Label l0 = new Label();
            mn.visitJumpInsn(IF_ICMPEQ, l0);
            mn.visitVarInsn(ALOAD, 0);
            mn.visitFieldInsn(GETFIELD, "pkg/de", "a", "I");
            mn.visitVarInsn(ALOAD, 1);
            mn.visitFieldInsn(GETFIELD, "pkg/de", "a", "I");
            mn.visitInsn(ISUB);
            mn.visitInsn(IRETURN);
            mn.visitLabel(l0);
            mn.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mn.visitVarInsn(ALOAD, 0);
            mn.visitFieldInsn(GETFIELD, "pkg/de", "b", "I");
            mn.visitVarInsn(ALOAD, 1);
            mn.visitFieldInsn(GETFIELD, "pkg/de", "b", "I");
            mn.visitInsn(ISUB);
            mn.visitInsn(IRETURN);
            mn.visitMaxs(2, 2);
            mn.visitEnd();
            node.methods.add(mn);

            mn = new MethodNode(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "compareTo", "(Ljava/lang/Object;)I", null, null);
            mn.visitCode();
            mn.visitVarInsn(ALOAD, 0);
            mn.visitVarInsn(ALOAD, 1);
            mn.visitTypeInsn(CHECKCAST, "pkg/de");
            mn.visitMethodInsn(INVOKEVIRTUAL, "pkg/de", "compareTo", "(Lpkg/de;)I");
            mn.visitInsn(IRETURN);
            mn.visitMaxs(2, 2);
            mn.visitEnd();
            node.methods.add(mn);
        }
        if (!node.name.equals("d")) return;

        MethodNode mtd = FFFixerImpl.getMethod(node, "b", "(Lcu;Lq;)V");

        Iterator<AbstractInsnNode> itr = mtd.instructions.iterator();
        while(itr.hasNext())
        {
            AbstractInsnNode insn = itr.next();
            if (insn instanceof MethodInsnNode)
            {
                MethodInsnNode v = (MethodInsnNode)insn;
                if (v.getOpcode() == INVOKEVIRTUAL && (v.owner + "/" + v.name + v.desc).equals("java/util/HashSet/iterator()Ljava/util/Iterator;"))
                {
                    insn = itr.next(); //Pop off the next which is ASTORE 15
                    FFFixerImpl.log.info("Injecting Var Order Fix");

                    VarInsnNode var = (VarInsnNode)insn;
                    InsnList toAdd = new InsnList();
                    toAdd.add(new VarInsnNode (ALOAD, var.var)); // var15 = fixInnerOrder(var15)
                    toAdd.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Util.class), "sortLvs", "(Ljava/util/Iterator;)Ljava/util/Iterator;", false));
                    toAdd.add(new VarInsnNode (ASTORE, var.var));

                    mtd.instructions.insert(insn, toAdd); // Inject static call
                    
                    inst.setWorkDone();
                    return;
                }                    
            }
        }
    }
}
