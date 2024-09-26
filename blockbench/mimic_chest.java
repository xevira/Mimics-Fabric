// Made with Blockbench 4.11.0
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package com.example.mod;
   
public class mimic_chest extends EntityModel<Entity> {
	private final ModelPart body;
	private final ModelPart top;
	public mimic_chest(ModelPart root) {
		this.body = root.getChild("body");
		this.top = root.getChild("top");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 19).cuboid(-7.0F, -10.0F, -7.0F, 14.0F, 10.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData top = body.addChild("top", ModelPartBuilder.create().uv(0, 0).cuboid(-7.0F, -4.0F, -14.0F, 14.0F, 5.0F, 14.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-1.0F, -1.0F, -15.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(3.0F, 0.0F, -12.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-5.0F, 0.0F, -12.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -10.0F, 7.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}