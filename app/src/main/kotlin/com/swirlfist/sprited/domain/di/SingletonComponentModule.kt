package com.swirlfist.sprited.domain.di

import com.swirlfist.sprited.domain.usecase.ApplyBucketUseCase
import com.swirlfist.sprited.domain.usecase.ApplyBucketUseCaseImpl
import com.swirlfist.sprited.domain.usecase.ClearEditorActionsUseCase
import com.swirlfist.sprited.domain.usecase.ClearEditorActionsUseCaseImpl
import com.swirlfist.sprited.domain.usecase.ConvertSelectionToFloatingImageUseCase
import com.swirlfist.sprited.domain.usecase.ConvertSelectionToFloatingImageUseCaseImpl
import com.swirlfist.sprited.domain.usecase.DeletePalettePresetUseCase
import com.swirlfist.sprited.domain.usecase.DeletePalettePresetUseCaseImpl
import com.swirlfist.sprited.domain.usecase.DeselectUseCase
import com.swirlfist.sprited.domain.usecase.DeselectUseCaseImpl
import com.swirlfist.sprited.domain.usecase.ExportPixelImageToSvgUseCase
import com.swirlfist.sprited.domain.usecase.ExportPixelImageToSvgUseCaseImpl
import com.swirlfist.sprited.domain.usecase.GetBasePixelImageUseCase
import com.swirlfist.sprited.domain.usecase.GetBasePixelImageUseCaseImpl
import com.swirlfist.sprited.domain.usecase.GetNextZoomFactorUseCase
import com.swirlfist.sprited.domain.usecase.GetNextZoomFactorUseCaseImpl
import com.swirlfist.sprited.domain.usecase.GetPalettePresetsUseCase
import com.swirlfist.sprited.domain.usecase.GetPalettePresetsUseCaseImpl
import com.swirlfist.sprited.domain.usecase.GetRectangleSelectionActionTypeUseCase
import com.swirlfist.sprited.domain.usecase.GetRectangleSelectionActionTypeUseCaseImpl
import com.swirlfist.sprited.domain.usecase.GetRedoEditorActionAvailableUseCase
import com.swirlfist.sprited.domain.usecase.GetRedoEditorActionAvailableUseCaseImpl
import com.swirlfist.sprited.domain.usecase.GetUndoEditorActionAvailableUseCase
import com.swirlfist.sprited.domain.usecase.GetUndoEditorActionAvailableUseCaseImpl
import com.swirlfist.sprited.domain.usecase.LandFloatingImageUseCase
import com.swirlfist.sprited.domain.usecase.LandFloatingImageUseCaseImpl
import com.swirlfist.sprited.domain.usecase.MoveFloatingImageUseCase
import com.swirlfist.sprited.domain.usecase.MoveFloatingImageUseCaseImpl
import com.swirlfist.sprited.domain.usecase.MoveImageUseCase
import com.swirlfist.sprited.domain.usecase.MoveImageUseCaseImpl
import com.swirlfist.sprited.domain.usecase.OpenPixelImageUseCase
import com.swirlfist.sprited.domain.usecase.OpenPixelImageUseCaseImpl
import com.swirlfist.sprited.domain.usecase.RedoEditorActionUseCase
import com.swirlfist.sprited.domain.usecase.RedoEditorActionUseCaseImpl
import com.swirlfist.sprited.domain.usecase.SavePalettePresetUseCase
import com.swirlfist.sprited.domain.usecase.SavePalettePresetUseCaseImpl
import com.swirlfist.sprited.domain.usecase.SavePixelImageUseCase
import com.swirlfist.sprited.domain.usecase.SavePixelImageUseCaseImpl
import com.swirlfist.sprited.domain.usecase.SelectWithMagicWandUseCase
import com.swirlfist.sprited.domain.usecase.SelectWithMagicWandUseCaseImpl
import com.swirlfist.sprited.domain.usecase.TogglePixelSelectionUseCase
import com.swirlfist.sprited.domain.usecase.TogglePixelSelectionUseCaseImpl
import com.swirlfist.sprited.domain.usecase.UndoEditorActionUseCase
import com.swirlfist.sprited.domain.usecase.UndoEditorActionUseCaseImpl
import com.swirlfist.sprited.domain.usecase.UpdateBasePixelImageUseCase
import com.swirlfist.sprited.domain.usecase.UpdateBasePixelImageUseCaseImpl
import com.swirlfist.sprited.domain.usecase.UpdatePixelColorUseCase
import com.swirlfist.sprited.domain.usecase.UpdatePixelColorUseCaseImpl
import com.swirlfist.sprited.domain.usecase.UpdateRectangleSelectionUseCase
import com.swirlfist.sprited.domain.usecase.UpdateRectangleSelectionUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("TooManyFunctions")
@Module
@InstallIn(SingletonComponent::class)
abstract class SingletonComponentModule {

    @Binds
    abstract fun bindUpdatePixelColorUseCase(
        impl: UpdatePixelColorUseCaseImpl,
    ): UpdatePixelColorUseCase

    @Binds
    abstract fun bindGetNextZoomFactorUseCase(
        impl: GetNextZoomFactorUseCaseImpl,
    ): GetNextZoomFactorUseCase

    @Binds
    abstract fun bindSavePixelImageUseCase(impl: SavePixelImageUseCaseImpl): SavePixelImageUseCase

    @Binds
    abstract fun bindExportPixelImageToSvgUseCase(
        impl: ExportPixelImageToSvgUseCaseImpl,
    ): ExportPixelImageToSvgUseCase

    @Binds
    abstract fun bindOpenPixelImageUseCase(impl: OpenPixelImageUseCaseImpl): OpenPixelImageUseCase

    @Binds
    abstract fun bindMoveImageUseCase(impl: MoveImageUseCaseImpl): MoveImageUseCase

    @Binds
    abstract fun bindApplyBucketUseCase(impl: ApplyBucketUseCaseImpl): ApplyBucketUseCase

    @Binds
    abstract fun bindGetUndoEditorActionAvailableUseCase(
        impl: GetUndoEditorActionAvailableUseCaseImpl,
    ): GetUndoEditorActionAvailableUseCase

    @Binds
    abstract fun bindGetRedoEditorActionAvailableUseCase(
        impl: GetRedoEditorActionAvailableUseCaseImpl,
    ): GetRedoEditorActionAvailableUseCase

    @Binds
    abstract fun bindUndoEditorActionUseCase(
        impl: UndoEditorActionUseCaseImpl,
    ): UndoEditorActionUseCase

    @Binds
    abstract fun bindRedoEditorActionUseCase(
        impl: RedoEditorActionUseCaseImpl,
    ): RedoEditorActionUseCase

    @Binds
    abstract fun bindClearEditorActionsUseCase(
        impl: ClearEditorActionsUseCaseImpl,
    ): ClearEditorActionsUseCase

    @Binds
    abstract fun bindGetBasePixelImageUseCase(
        impl: GetBasePixelImageUseCaseImpl,
    ): GetBasePixelImageUseCase

    @Binds
    abstract fun bindUpdateBasePixelImageUseCase(
        impl: UpdateBasePixelImageUseCaseImpl,
    ): UpdateBasePixelImageUseCase

    @Binds
    abstract fun bindGetPalettePresetsUseCase(
        impl: GetPalettePresetsUseCaseImpl,
    ): GetPalettePresetsUseCase

    @Binds
    abstract fun bindSavePalettePresetUseCase(
        impl: SavePalettePresetUseCaseImpl,
    ): SavePalettePresetUseCase

    @Binds
    abstract fun bindDeletePalettePresetUseCase(
        impl: DeletePalettePresetUseCaseImpl,
    ): DeletePalettePresetUseCase

    @Binds
    abstract fun bindTogglePixelSelectionUseCase(
        impl: TogglePixelSelectionUseCaseImpl,
    ): TogglePixelSelectionUseCase

    @Binds
    abstract fun bindSelectWithMagicWandUseCase(
        impl: SelectWithMagicWandUseCaseImpl,
    ): SelectWithMagicWandUseCase

    @Binds
    abstract fun bindGetRectangleSelectionActionTypeUseCase(
        impl: GetRectangleSelectionActionTypeUseCaseImpl,
    ): GetRectangleSelectionActionTypeUseCase

    @Binds
    abstract fun bindUpdateRectangleSelectionUseCase(
        impl: UpdateRectangleSelectionUseCaseImpl,
    ): UpdateRectangleSelectionUseCase

    @Binds
    abstract fun bindDeselectUseCase(impl: DeselectUseCaseImpl): DeselectUseCase

    @Binds
    abstract fun bindConvertSelectionToFloatingImageUseCase(
        impl: ConvertSelectionToFloatingImageUseCaseImpl,
    ): ConvertSelectionToFloatingImageUseCase

    @Binds
    abstract fun bindLandFloatingImageUseCase(
        impl: LandFloatingImageUseCaseImpl,
    ): LandFloatingImageUseCase

    @Binds
    abstract fun bindMoveFloatingImageUseCase(
        impl: MoveFloatingImageUseCaseImpl,
    ): MoveFloatingImageUseCase
}
