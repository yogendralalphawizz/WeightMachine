package utils;

//import com.besthealth.bhBodyComposition120.BhBodyComposition;
//import com.besthealth.bhBodyComposition120.BhErrorType;
//import com.besthealth.bhBodyComposition120.BhSex;
//import com.holtek.libHTBodyfat.HTBodyBasicInfo;
//import com.holtek.libHTBodyfat.HTBodyResultAllBody;

import java.util.Locale;

/*
import aicare.net.cn.sdk.ailinksdkdemoandroid.EightBodyfatAdc;
*/


public class EightBodyFatAlgorithms {


    public EightBodyFatAlgorithms() {
    }

    private static class AlgorithmsHolder {
        private static EightBodyFatAlgorithms algorithmsUnit = new EightBodyFatAlgorithms();
    }


    public static EightBodyFatAlgorithms getInstance() {
        return AlgorithmsHolder.algorithmsUnit;

    }


 /*   public EightBodyFatBean getAlgorithmsData(int algorithms, int sex, int height, float weight_kg, int age, EightBodyfatAdc eightBodyfatAdc) {
        EightBodyFatBean eightBodyFatBean = new EightBodyFatBean();
        switch (algorithms) {

            case 0x20:
                //实例化对象
                BhBodyComposition bhBodyComposition = new BhBodyComposition();
                //传入性别
                bhBodyComposition.bhSex = sex == 1 ? BhSex.MALE.ordinal() : BhSex.FEMALE.ordinal();
                //传入体重。单位是千克。其他单位需要自己转换
                bhBodyComposition.bhWeightKg = weight_kg;
                //传入年龄
                bhBodyComposition.bhAge = age;
                //传入身高
                bhBodyComposition.bhHeightCm = height;
                //传入阻抗。阻抗值通过设备获取。加密数据
                //全身阻抗
                bhBodyComposition.bhZLeftBodyEnCode = eightBodyfatAdc.getAdcRightBody();
                //左手阻抗
                bhBodyComposition.bhZLeftArmEnCode = eightBodyfatAdc.getAdcLeftHand();
                //右手阻抗
                bhBodyComposition.bhZRightArmEnCode = eightBodyfatAdc.getAdcRightHand();
                //左脚阻抗
                bhBodyComposition.bhZLeftLegEnCode = eightBodyfatAdc.getAdcLeftFoot();
                //右脚阻抗
                bhBodyComposition.bhZRightLegEnCode = eightBodyfatAdc.getAdcRightFoot();
                //校验传入的值
                BhErrorType bhErrorType = BhErrorType.values()[bhBodyComposition.getBodyComposition()];

                if (bhErrorType == BhErrorType.NONE) {
                    eightBodyFatBean.setBhSkeletalMuscleKg(bhBodyComposition.bhSkeletalMuscleKg + "");
                    eightBodyFatBean.setBhSkeletalMuscleKgLevel(bhBodyComposition.bhSkeletalMuscleKgLevel + "");
                    eightBodyFatBean.setBhSkeletalMuscleKgListStandardOrExcellent(bhBodyComposition.bhMuscleKgListStandardOrExcellent + "");
                    eightBodyFatBean.setBhSkeletalMuscleKgListUnderOrStandard(bhBodyComposition.bhSkeletalMuscleKgListUnderOrStandard + "");
                }

                break;


            case 1:
            default:

                HTBodyBasicInfo basicInfo = new HTBodyBasicInfo(sex, height, weight_kg, age);
                basicInfo.htZAllBodyImpedance = eightBodyfatAdc.getAdcRightBody();
                basicInfo.htZLeftLegImpedance = eightBodyfatAdc.getAdcLeftFoot();
                basicInfo.htZRightLegImpedance = eightBodyfatAdc.getAdcRightFoot();
                basicInfo.htZLeftArmImpedance = eightBodyfatAdc.getAdcLeftHand();
                basicInfo.htZRightArmImpedance = eightBodyfatAdc.getAdcRightHand();
                basicInfo.htTwoLegsImpedance = eightBodyfatAdc.getAdcFoot();
                basicInfo.htTwoArmsImpedance = eightBodyfatAdc.getAdcHand();
                HTBodyResultAllBody resultTwoLegs = new HTBodyResultAllBody();
                int errorType = resultTwoLegs.getBodyfatWithBasicInfo(basicInfo);
                if (errorType == HTBodyBasicInfo.ErrorNone) {
                    eightBodyFatBean.setBmi(Adecimal(resultTwoLegs.htBMI));
                    eightBodyFatBean.setBmr((float) resultTwoLegs.htBMR);
                    eightBodyFatBean.setUvi((float) resultTwoLegs.htVFAL);
                    eightBodyFatBean.setBm(String.valueOf((float) resultTwoLegs.htBoneKg));
                    eightBodyFatBean.setBfr(Adecimal(resultTwoLegs.htBodyfatPercentage));
                    eightBodyFatBean.setVwc(Adecimal(resultTwoLegs.htWaterPercentage));
                    eightBodyFatBean.setRom(Adecimal(resultTwoLegs.htMusclePercentage));
                    eightBodyFatBean.setBodyAge(resultTwoLegs.htBodyAge);
                    eightBodyFatBean.setPp(Adecimal(resultTwoLegs.htProteinPercentage));
                    eightBodyFatBean.setSfr(Adecimal(resultTwoLegs.htBodyfatSubcut));
                    eightBodyFatBean.setFatMassBody(String.valueOf(resultTwoLegs.htBodyfatKgTrunk));
                    eightBodyFatBean.setFatMassLeftTop(String.valueOf(resultTwoLegs.htBodyfatKgLeftArm));
                    eightBodyFatBean.setFatMassLeftBottom(String.valueOf(resultTwoLegs.htBodyfatKgLeftLeg));
                    eightBodyFatBean.setFatMassRightTop(String.valueOf(resultTwoLegs.htBodyfatKgRightArm));
                    eightBodyFatBean.setFatMassRightBottom(String.valueOf(resultTwoLegs.htBodyfatKgRightLeg));
//                    eightBodyFatBean.setFatMass(resultTwoLegs.htBodyfatKg);
                    eightBodyFatBean.setMuscleMassBody(String.valueOf(resultTwoLegs.htMuscleKgTrunk));
                    eightBodyFatBean.setMuscleMassLeftTop(String.valueOf(resultTwoLegs.htMuscleKgLeftArm));
                    eightBodyFatBean.setMuscleMassLeftBottom(String.valueOf(resultTwoLegs.htMuscleKgLeftLeg));
                    eightBodyFatBean.setMuscleMassRightTop(String.valueOf(resultTwoLegs.htMuscleKgRightArm));
                    eightBodyFatBean.setMuscleMassRightBottom(String.valueOf(resultTwoLegs.htMuscleKgRightLeg));


//                    eightBodyFatBean.setMusleMass(resultTwoLegs.htMuscleKg);
//                    eightBodyFatBean.setStandardWeight(resultTwoLegs.htIdealWeightKg);
//                    eightBodyFatBean.setWeightWithoutFat(resultTwoLegs.htBodyfatFreeMass);
//                    eightBodyFatBean.setWeightControl((weight_kg - resultTwoLegs.htIdealWeightKg));
//                    eightBodyFatBean.setFatLevel(HealthyStatusUtil.ObesitylevelsStatus(weight_kg, (float) resultTwoLegs.htIdealWeightKg));
//                    double muscle = (resultTwoLegs.htMuscleKgLeftArm + resultTwoLegs.htMuscleKgLeftLeg + resultTwoLegs.htMuscleKgRightArm + resultTwoLegs.htMuscleKgRightLeg);
//                    eightBodyFatBean.setMusleMassLimbs(Adecimal(muscle / (height * height) * 10000));

                    eightBodyFatBean.setAdcFoot((resultTwoLegs.htZLeftLeg + resultTwoLegs.htZRightLeg));
                    eightBodyFatBean.setAdcHand((resultTwoLegs.htZLeftArm + resultTwoLegs.htZRightArm));
                    eightBodyFatBean.setAdcLeftHand(resultTwoLegs.htZLeftArm);
                    eightBodyFatBean.setAdcRightHand(resultTwoLegs.htZRightArm);
                    eightBodyFatBean.setAdcLeftFoot(resultTwoLegs.htZLeftLeg);
                    eightBodyFatBean.setAdcRightFoot(resultTwoLegs.htZRightLeg);
                    eightBodyFatBean.setAdcLeftBody(resultTwoLegs.htZAllBody);
                    eightBodyFatBean.setAdcRightBody(resultTwoLegs.htZAllBody);
                    eightBodyFatBean.setAdcRightHandLeftFoot((resultTwoLegs.htZRightArm + resultTwoLegs.htZLeftLeg));
                    eightBodyFatBean.setAdcLeftHandRightFoot((resultTwoLegs.htZLeftArm + resultTwoLegs.htZRightLeg));
                    eightBodyFatBean.setAdcBody(resultTwoLegs.htZAllBody);

                }


        }
        return eightBodyFatBean;
    }
*/

    private float Adecimal(double data) {


        return Float.parseFloat(String.format(Locale.US, "%.1f", data));


    }
}
