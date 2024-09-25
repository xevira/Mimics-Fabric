package github.xevira.mimics.util;

public class MimicDamage {
    public static final MimicDamage MIMIC = new MimicDamage(0.75F, 0.5F, 0.25F);
    private final float lowMimicDamageThreshold;
    private final float mediumMimicDamageThreshold;
    private final float highMimicDamageThreshold;

    private MimicDamage(float lowMimicDamageThreshold, float mediumMimicDamageThreshold, float highMimicDamageThreshold) {
        this.lowMimicDamageThreshold = lowMimicDamageThreshold;
        this.mediumMimicDamageThreshold = mediumMimicDamageThreshold;
        this.highMimicDamageThreshold = highMimicDamageThreshold;
    }

    public DamageLevel getMimicDamageLevel(float health) {
        if (health < this.highMimicDamageThreshold) {
            return MimicDamage.DamageLevel.HIGH;
        } else if (health < this.mediumMimicDamageThreshold) {
            return MimicDamage.DamageLevel.MEDIUM;
        } else {
            return health < this.lowMimicDamageThreshold ? DamageLevel.LOW : DamageLevel.NONE;
        }
    }

    public DamageLevel getMimicDamageLevel(int currentDamage, int maxDamage) {
        return this.getMimicDamageLevel((float)(maxDamage - currentDamage) / (float)maxDamage);
    }

    public enum DamageLevel {
        NONE,
        LOW,
        MEDIUM,
        HIGH;
    }
}
