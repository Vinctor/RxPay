package com.vinctor.annotation;

/**
 * Created by Vinctor on 2017/3/14.
 */

import com.Vinctor.VLog;
import com.Vinctor.Vprocessor;
import com.Vinctor.bean.ClassEntity;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class WxProcessor extends Vprocessor {
    private final String packageNameTag = "wxapi";
    private final String customeClassName = "WXPayEntryActivity";

    @Override
    protected Class<? extends Annotation>[] getSupportedAnnotations() {
        return new Class[]{WxPay.class};
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        VLog.setDebug(true);
        //build
        Map<String, ClassEntity> map = entityHandler.handlerElement(roundEnv, this);
        //
        for (Map.Entry<String, ClassEntity> item : map.entrySet()) {
            entityHandler.generateCode(brewWxEntityActivity(item));
            entityHandler.generateCode(brewWxResgister(item));
            break;
        }

        return true;
    }


    private JavaFile brewWxEntityActivity(Map.Entry<String, ClassEntity> item) {
        ClassEntity classEntity = item.getValue();

        String packageName = classEntity.getElement().getAnnotation(WxPay.class).value()
                + "." + packageNameTag;

        ClassName activityClazz = ClassName.get("android.app", "Activity");
        ClassName interfaceClazz = ClassName.get("com.tencent.mm.opensdk.openapi", "IWXAPIEventHandler");
        ClassName baseReqClazz = ClassName.get("com.tencent.mm.opensdk.modelbase", "BaseReq");
        ClassName baseRespClazz = ClassName.get("com.tencent.mm.opensdk.modelbase", "BaseResp");
        ClassName onNewIntentClazz = ClassName.get("android.content", "Intent");
        ClassName bundleClazz = ClassName.get("android.os", "Bundle");
        ClassName wxApiFactoryClazz = ClassName.get("com.tencent.mm.opensdk.openapi", "WXAPIFactory");
        ClassName wxApiClazz = ClassName.get("com.tencent.mm.opensdk.openapi", "IWXAPI");
        ClassName rxWxPay = ClassName.get("com.vinctor.pay", "RxWxPay");
        ClassName busClazz = ClassName.get("com.vinctor.pay.utils", "BusUtil");


        MethodSpec onReq = MethodSpec
                .methodBuilder("onReq")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(baseReqClazz, "baseReq")
                .build();

        MethodSpec onCreate = MethodSpec
                .methodBuilder("onCreate")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(bundleClazz, "savedInstanceState")
                .addStatement("\tsuper.onCreate(savedInstanceState);\n" +
                                "api = $T.createWXAPI(this, $T.getIntance().getAppid());\n" +
                                "api.handleIntent(getIntent(), this)"
                        , wxApiFactoryClazz, rxWxPay)
                .build();

        MethodSpec onNewIntent = MethodSpec
                .methodBuilder("onNewIntent")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .returns(void.class)
                .addParameter(onNewIntentClazz, "intent")
                .addStatement("\tsuper.onNewIntent(intent);\n" +
                        " setIntent(intent);\n" +
                        "api.handleIntent(intent, this)")
                .build();

        MethodSpec onResp = MethodSpec
                .methodBuilder("onResp")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(baseRespClazz, "baseResp")
                .addStatement("\t$T.getDefault().post(baseResp);\n" +
                        "finish()", busClazz)
                .build();


        TypeSpec typeSpec = TypeSpec
                .classBuilder(customeClassName)
                .addModifiers(Modifier.PUBLIC)
                .superclass(activityClazz)
                .addSuperinterface(interfaceClazz)
                .addField(wxApiClazz, "api", Modifier.PRIVATE)
                .addMethod(onCreate)
                .addMethod(onNewIntent)
                .addMethod(onReq)
                .addMethod(onResp)
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .build();

        return javaFile;
    }

    private JavaFile brewWxResgister(Map.Entry<String, ClassEntity> item) {
        ClassEntity classEntity = item.getValue();

        String packageName = classEntity.getElement().getAnnotation(WxPay.class).value();

        ClassName receiveClazz = ClassName.get("android.content", "BroadcastReceiver");
        ClassName contextClazz = ClassName.get("android.content", "Context");
        ClassName bundleClazz = ClassName.get("android.os", "Bundle");
        ClassName wxApiFactoryClazz = ClassName.get("com.tencent.mm.opensdk.openapi", "WXAPIFactory");
        ClassName wxApiClazz = ClassName.get("com.tencent.mm.opensdk.openapi", "IWXAPI");
        ClassName rxWxPay = ClassName.get("com.vinctor.pay", "RxWxPay");
        ClassName intentClazz = ClassName.get("android.content", "Intent");


        MethodSpec onReceive = MethodSpec
                .methodBuilder("onReceive")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(contextClazz, "context")
                .addParameter(intentClazz, "intent")
                .addStatement("$T msgApi = $T.createWXAPI(context, null);\n" +
                                "msgApi.registerApp($T.getIntance().getAppid())"
                        , wxApiClazz, wxApiFactoryClazz, rxWxPay)
                .build();


        TypeSpec typeSpec = TypeSpec
                .classBuilder("AppRegister")
                .addModifiers(Modifier.PUBLIC)
                .superclass(receiveClazz)
                .addMethod(onReceive)
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .build();

        return javaFile;
    }
}
