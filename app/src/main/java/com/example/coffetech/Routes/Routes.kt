// Routes.kt

package com.example.coffetech.Routes

import androidx.compose.foundation.interaction.DragInteraction
import com.example.coffetech.viewmodel.Collaborator.Collaborator

object Routes {
    //Auth
    const val LoginView = "loginScreen"
    const val RegisterView = "registerScreen"
    const val ForgotPasswordView = "forgotPassword"
    const val ConfirmTokenForgotPasswordView = "confirmTokenForgotPassword"
    const val VerifyAccountView = "verifyAccount"
    const val AlertSendView = "alertSend"
    const val NewPasswordView = "newPassword"
    const val StartView = "startView"
    const val ProfileView = "profileView"
    const val ChangePasswordView ="changePasswordView"
    const val RegisterPasswordView = "registerPasswordView"

    //Farms
    const val FarmView = "farmView"
    const val FarmInformationView = "farmInformationView"
    const val FarmEditView ="farmEditView"
    const val CreateFarmView ="createFarmView"

    //Collaborators
    const val CollaboratorView = "collaboratorView"
    const val NotificationView = "notificationView"
    const val AddCollaboratorView = "addCollaboratorView"
    const val EditCollaboratorView ="editCollaboratorView"

    //Plots
    const val CreateMapPlotView = "createMapPlotView("
    const val PlotInformationView ="PlotInformationView"
    const val CreatePlotInformationView = "createPlotInformationView"
    const val EditPlotInformationView = "editPlotInformationView"
    const val EditMapPlotView = "editMapPlotView"

    //Flowering
    const val FloweringInformationView = "floweringInformationView"
    const val AddFloweringView = "AddFloweringView"
    const val EditFloweringView= "editFloweringView"
    const val RecommendationFloweringViewPreview = "recommendationFloweringViewPreview"

    //CulturalWorkTask
    const val CulturalWorkTaskInformationView = "culturalWorkTaskInformationView"
    const val AddCulturalWorkView1 = "addCulturalWorkView1"
    const val  AddCulturalWorkView2 = "addCulturalWorkView2"
    const val  ReminderCulturalWorkView = "reminderCulturalWorkView"
    const val  CulturalWorkTaskGeneralView = "culturalWorkTaskGeneralView"
    const val  EditCulturalWorkView = "editCulturalWorkView"

    //Transaction
    const val TransactionInformationView = "transactionInformationView"
    const val AddTransactionView = "AddTransactionView"
    const val EditTransactionView = "EditTransactionView"

    //Reports
    const val ReportsSelectionView = "ReportsSelectionView"
    const val FormFinanceReportView ="FormFinanceReportView"
    const val FormDetectionReportView = "FormDetectionReportView"
    const val DetectionReportView = "DetectionReportView"


    //HealthCheck
    const val SendDectectionView = "SendDectectionView"
    const val ResultHealthCheckView ="ResultHealthCheckView"
    const val  DetectionHistoryView = "DetectionHistoryView"
    const val  EditResultHealthCheckView = "EditResultHealthCheckView "
}
