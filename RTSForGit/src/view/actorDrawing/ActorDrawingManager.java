/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.actorDrawing;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.HashMap;
import model.army.ArmyManager;
import model.army.data.Actor;
import model.army.data.actors.AnimationActor;
import model.army.data.actors.ModelActor;
import model.army.data.actors.ParticleActor;
import model.army.data.actors.RagdollActor;
import model.army.data.actors.ProjectileActor;
import model.army.data.actors.UnitActor;
import tools.LogUtil;
import view.material.MaterialManager;

/**
 *
 * @author Benoît
 */
public class ActorDrawingManager implements AnimEventListener {

    AssetManager assetManager;
    MaterialManager materialManager;
    
    ArmyManager armyManager;
    public Node mainNode;
    public PhysicsSpace mainPhysicsSpace;
    
    ModelActorDrawer modelDrawer;
    ParticleActorDrawer particleDrawer;
    AnimationActorDrawer animationDrawer;
    RagdollActorDrawer physicDrawer;
    
    
    HashMap<String, Spatial> models = new HashMap<>();

    public ActorDrawingManager(AssetManager assetManager, MaterialManager materialManager, ArmyManager armyManager){
        this.assetManager = assetManager;
        this.materialManager = materialManager;
        this.armyManager = armyManager;
        mainNode = new Node();
        
        modelDrawer = new ModelActorDrawer(this);
        particleDrawer = new ParticleActorDrawer(this);
        animationDrawer = new AnimationActorDrawer();
        physicDrawer = new RagdollActorDrawer(this);
   }
    
    public void render(){
        // first, the spatials attached to interrupted actor are detached
        for(Actor a : armyManager.grabDeletedActors()){
            if(a.viewElements.spatial != null){
                mainNode.detachChild(a.viewElements.spatial);
            }
            if(a.viewElements.particleEmitter != null)
                a.viewElements.particleEmitter.setParticlesPerSec(0);
            if(a.viewElements.selectionCircle != null)
                mainNode.detachChild(a.viewElements.selectionCircle);
        }
        
        for(Actor a : armyManager.getActors()){
            switch (a.getType()){
                case "default" : break;
                case "physic" : physicDrawer.draw((RagdollActor)a); break;
                case "unit" : modelDrawer.draw((UnitActor)a); break;
                case "projectile" : modelDrawer.draw((ProjectileActor)a); break;
                case "animation" : animationDrawer.draw((AnimationActor)a); break;
                case "particle" : particleDrawer.draw((ParticleActor)a); break;
            }
        }
    }
    
    protected Spatial buildSpatial(String modelPath){
        if(!models.containsKey(modelPath))
            models.put(modelPath, assetManager.loadModel("models/"+modelPath));
        Spatial res = models.get(modelPath).clone();
        if(res == null)
            LogUtil.logger.info(modelPath);
        AnimControl control = res.getControl(AnimControl.class);
        if(control != null){
            control.addListener(this);
            control.createChannel();
        }
        mainNode.attachChild(res);
        return res;
    }
    
    protected Material getParticleMat(String texturePath){
        Material m = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        m.setTexture("Texture", assetManager.loadTexture("textures/"+texturePath));
        return m;
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }

}