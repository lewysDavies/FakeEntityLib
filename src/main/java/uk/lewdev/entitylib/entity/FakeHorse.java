package uk.lewdev.entitylib.entity;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import uk.lewdev.entitylib.entity.protocol.FakeLivingEntity;
import uk.lewdev.entitylib.utils.MaskUtil;

/**
 * https://wiki.vg/Entity_metadata#Abstract_Horse
 * 
 * @author Lewys Davies (Lew_)
 */
public class FakeHorse extends FakeLivingEntity {

    private final WrappedDataWatcherObject horseByteWatcher = new WrappedDataWatcherObject(16, Registry.get(Byte.class));
    private byte horseByte = (byte) 0;
    
    private final WrappedDataWatcherObject horseTypeWatcher = new WrappedDataWatcherObject(18, Registry.get(Integer.class));
    private HorseColor horseColor = HorseColor.WHITE;
    private HorseStyle horseStyle = HorseStyle.NONE;
    
    protected FakeHorse(UUID uuid, World world, double x, double y, double z) {
        super(EntityType.HORSE, uuid, world, x, y, z);
        this.initDataWatcherObjs();
    }
    
    protected void initDataWatcherObjs() {
        super.getDataWatcher().setObject(this.horseByteWatcher, this.horseByte);
        super.getDataWatcher().setObject(this.horseTypeWatcher, toVariantId(this.horseColor, this.horseStyle));
    }
    
    public boolean isTame() {
        return MaskUtil.getBit(this.horseByte, 1);
    }
    
    public boolean isSaddled() {
        return MaskUtil.getBit(this.horseByte, 2);
    }
    
    public boolean isEating() {
        return MaskUtil.getBit(this.horseByte, 4);
    }
    
    public boolean isRearing() {
        return MaskUtil.getBit(this.horseByte, 5);
    }
    
    public boolean isMouthOpen() {
        return MaskUtil.getBit(this.horseByte, 6);
    }
    
    public HorseColor getColor() {
        return this.horseColor;
    }
    
    public HorseStyle getStyle() {
        return this.horseStyle;
    }
    
    public void setTame(boolean tame) {
        super.assertNotDead();
        
        this.horseByte = MaskUtil.setBit(this.horseByte, 1, tame);
        super.getDataWatcher().setObject(this.horseByteWatcher, this.horseByte);
        super.sendMetaUpdate();
    }
    
    public void setSaddled(boolean saddled) {
        super.assertNotDead();
        
        this.horseByte = MaskUtil.setBit(this.horseByte, 2, saddled);
        super.getDataWatcher().setObject(this.horseByteWatcher, this.horseByte);
        super.sendMetaUpdate();
    }
    
    public void setEating(boolean eating) {
        super.assertNotDead();
        
        this.horseByte = MaskUtil.setBit(this.horseByte, 4, eating);
        super.getDataWatcher().setObject(this.horseByteWatcher, this.horseByte);
        super.sendMetaUpdate();
    }
    
    public void setRearing(boolean rearing) {
        super.assertNotDead();
        
        this.horseByte = MaskUtil.setBit(this.horseByte, 5, rearing);
        super.getDataWatcher().setObject(this.horseByteWatcher, this.horseByte);
        super.sendMetaUpdate();
    }
    
    public void setMouthOpen(boolean mouthOpen) {
        super.assertNotDead();
        
        this.horseByte = MaskUtil.setBit(this.horseByte, 6, mouthOpen);
        super.getDataWatcher().setObject(this.horseByteWatcher, this.horseByte);
        super.sendMetaUpdate();
    }
    
    public void setHorseVarient(HorseColor color, HorseStyle style) {
        this.horseColor = color;
        this.horseStyle = style;
        
        int variant = toVariantId(color, style);
        super.getDataWatcher().setObject(this.horseTypeWatcher, variant);
        super.sendMetaUpdate();
    }
    
    public static enum HorseStyle {
        NONE,
        WHITE,
        WHITEFIELD,
        WHITE_DOTS,
        BLACK_DOTS
    }
    
    public static enum HorseColor {
        WHITE,
        CREAMY,
        CHESTNUT,
        BROWN,
        BLACK,
        GRAY,
        DARK_BROWN;
    }
    
    private static int toVariantId(HorseColor color, HorseStyle style) {
        return color.ordinal() | style.ordinal() << 8;
    }
}
