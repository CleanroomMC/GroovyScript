
import com.cleanroommc.groovyscript.TestClass
import com.cleanroommc.groovyscript.api.GroovyLog

@Mixin(value = TestClass.class, remap=false)
class TestClassMixin {

    @Inject(method = "sayHello", at = @At("HEAD"), cancellable = true)
    private static void sayBye(CallbackInfo ci) {
        GroovyLog.get().info("Bye from TestClassMixin");
        ci.cancel();
    }

}
