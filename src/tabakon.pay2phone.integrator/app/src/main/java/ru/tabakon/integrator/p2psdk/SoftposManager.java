package ru.tabakon.integrator.p2psdk;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ru.tinkoff.posterminal.p2psdk.Callback;
import ru.tinkoff.posterminal.p2psdk.PaymentMethod;
import ru.tinkoff.posterminal.p2psdk.R.string;
import ru.tinkoff.posterminal.p2psdk.SoftposException;
import ru.tinkoff.posterminal.p2psdk.SoftposInfo;
import ru.tinkoff.posterminal.p2psdk.ValidationType;
import ru.tinkoff.posterminal.p2psdk.ValidationType.Valid;

@Metadata(
        mv = {1, 9, 0},
        k = 1,
        xi = 48,
        d1 = {"\u0000Q\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007*\u0001\u0004\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J.\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0002J\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015J\u0018\u0010\u0016\u001a\u00020\u00172\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u0014\u001a\u00020\u0015H\u0002J(\u0010\u0018\u001a\u00020\u00192\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u001a\u001a\u00020\r2\u0006\u0010\u001b\u001a\u00020\r2\u0006\u0010\u0014\u001a\u00020\u0015H\u0002J(\u0010\u001c\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\u000e\u001a\u00020\u000fJ&\u0010\u001d\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0006\u001a\u00020\u0007J6\u0010\u001e\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0002J6\u0010\u001f\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0002R\u0010\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0005R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006 "},
        d2 = {"Lru/tinkoff/posterminal/p2psdk/SoftposManager;", "", "()V", "broadcastReceiver", "ru/tinkoff/posterminal/p2psdk/SoftposManager$broadcastReceiver$1", "Lru/tinkoff/posterminal/p2psdk/SoftposManager$broadcastReceiver$1;", "callback", "Lru/tinkoff/posterminal/p2psdk/Callback;", "buildIntent", "Landroid/content/Intent;", "isRefund", "", "amount", "", "paymentMethod", "Lru/tinkoff/posterminal/p2psdk/PaymentMethod;", "softposInfo", "Lru/tinkoff/posterminal/p2psdk/SoftposInfo;", "clear", "", "context", "Landroid/content/Context;", "getAmountString", "", "isValidAmount", "Lru/tinkoff/posterminal/p2psdk/ValidationType;", "minAmount", "maxAmount", "payToPhone", "refund", "startActivity", "startActivityIfPossible", "p2psdk_release"}
)
public final class SoftposManager {
    @NotNull
    public static final SoftposManager INSTANCE = new SoftposManager();
    @Nullable
    private static Callback callback;
    @NotNull
    private static final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(@Nullable Context var1, @Nullable Intent var2) {
            // $FF: Couldn't be decompiled
        }
    };

    private SoftposManager() {
    }

    public final void payToPhone(@NotNull Context context, long amount, @NotNull Callback callback, @NotNull PaymentMethod paymentMethod) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(callback, "callback");
        Intrinsics.checkNotNullParameter(paymentMethod, "paymentMethod");
        SoftposManager.callback = callback;
        startActivityIfPossible$default(this, context, amount, false, paymentMethod, (SoftposInfo)null, 16, (Object)null);
    }

    // $FF: synthetic method
    public static void payToPhone$default(SoftposManager var0, Context var1, long var2, Callback var4, PaymentMethod var5, int var6, Object var7) {
        if ((var6 & 8) != 0) {
            var5 = PaymentMethod.UNDEFINED;
        }

        var0.payToPhone(var1, var2, var4, var5);
    }

    public final void refund(@NotNull Context context, long amount, @NotNull SoftposInfo softposInfo, @NotNull Callback callback) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(softposInfo, "softposInfo");
        Intrinsics.checkNotNullParameter(callback, "callback");
        SoftposManager.callback = callback;
        PaymentMethod var6 = softposInfo.getPaymentMethod();
        this.startActivityIfPossible(context, amount, true, var6, softposInfo);
    }

    public final void clear(@NotNull Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        context.unregisterReceiver((BroadcastReceiver)broadcastReceiver);
        callback = null;
    }

    private final void startActivityIfPossible(Context context, long amount, boolean isRefund, PaymentMethod paymentMethod, SoftposInfo softposInfo) {
        ValidationType validationType = this.isValidAmount(amount, paymentMethod.minAmount(), paymentMethod.maxAmount(), context);
        Callback var10000;
        if (isRefund) {
            if (softposInfo instanceof SoftposInfo.Nfc) {
                if (StringsKt.isBlank((CharSequence)((SoftposInfo.Nfc)softposInfo).getRrn())) {
                    var10000 = callback;
                    if (var10000 != null) {
                        var10000.onError((Throwable)(new SoftposException.ValidationException("rrn should not be empty")));
                    }

                    return;
                }
            } else {
                if (!(softposInfo instanceof SoftposInfo.Qr)) {
                    var10000 = callback;
                    if (var10000 != null) {
                        var10000.onError((Throwable)(new SoftposException.ValidationException("Incorrect SoftposResult or null")));
                    }

                    return;
                }

                if (((SoftposInfo.Qr)softposInfo).getPaymentId() <= 0L) {
                    var10000 = callback;
                    if (var10000 != null) {
                        var10000.onError((Throwable)(new SoftposException.ValidationException("paymentId should not be less or equal 0")));
                    }

                    return;
                }
            }
        }

        if (validationType instanceof ValidationType.TooLargeAmount) {
            var10000 = callback;
            if (var10000 != null) {
                var10000.onError((Throwable)(new SoftposException.ValidationException(((ValidationType.TooLargeAmount)validationType).getMessage())));
            }
        } else if (validationType instanceof ValidationType.TooSmallAmount) {
            var10000 = callback;
            if (var10000 != null) {
                var10000.onError((Throwable)(new SoftposException.ValidationException(((ValidationType.TooSmallAmount)validationType).getMessage())));
            }
        } else if (Intrinsics.areEqual(validationType, Valid.INSTANCE)) {
            this.startActivity(context, amount, isRefund, paymentMethod, softposInfo);
        }

    }

    // $FF: synthetic method
    static void startActivityIfPossible$default(SoftposManager var0, Context var1, long var2, boolean var4, PaymentMethod var5, SoftposInfo var6, int var7, Object var8) {
        if ((var7 & 8) != 0) {
            var5 = PaymentMethod.UNDEFINED;
        }

        if ((var7 & 16) != 0) {
            var6 = null;
        }

        var0.startActivityIfPossible(var1, var2, var4, var5, var6);
    }

    private final void startActivity(Context context, long amount, boolean isRefund, PaymentMethod paymentMethod, SoftposInfo softposInfo) {
        context.registerReceiver((BroadcastReceiver)broadcastReceiver, new IntentFilter("ru.tinkoff.posterminal.broadcast.RESULT_TRANSACTION"));

        try {
            Intent intent = this.buildIntent(isRefund, amount, paymentMethod, softposInfo);
            context.startActivity(intent);
        } catch (ActivityNotFoundException var8) {
            Callback var10000 = callback;
            if (var10000 != null) {
                var10000.onError((Throwable)var8);
            }
        }

    }

    // $FF: synthetic method
    static void startActivity$default(SoftposManager var0, Context var1, long var2, boolean var4, PaymentMethod var5, SoftposInfo var6, int var7, Object var8) {
        if ((var7 & 8) != 0) {
            var5 = PaymentMethod.UNDEFINED;
        }

        if ((var7 & 16) != 0) {
            var6 = null;
        }

        var0.startActivity(var1, var2, var4, var5, var6);
    }

    private final ValidationType isValidAmount(long amount, long minAmount, long maxAmount, Context context) {
        ValidationType var10000;
        String var10002;
        int var10003;
        Object[] var8;
        if (amount < minAmount) {
            var10003 = string.pay_to_phone_sdk_min_amount;
            var8 = new Object[]{this.getAmountString(minAmount, context)};
            var10002 = context.getString(var10003, var8);
            Intrinsics.checkNotNullExpressionValue(var10002, "getString(...)");
            var10000 = (ValidationType)(new ValidationType.TooSmallAmount(var10002));
        } else if (amount > maxAmount) {
            var10003 = string.pay_to_phone_sdk_max_amount;
            var8 = new Object[]{String.valueOf(maxAmount)};
            var10002 = context.getString(var10003, var8);
            Intrinsics.checkNotNullExpressionValue(var10002, "getString(...)");
            var10000 = (ValidationType)(new ValidationType.TooLargeAmount(var10002));
        } else {
            var10000 = (ValidationType)Valid.INSTANCE;
        }

        return var10000;
    }

    private final String getAmountString(long amount, Context context) {
        long rub = amount / (long)100;
        long cop = amount % (long)100;
        String var10000;
        int var10001;
        String var8;
        Object[] var9;
        if (cop == 0L) {
            var10001 = string.pay_to_phone_sdk_amount_part_rub;
            var9 = new Object[]{rub};
            var8 = context.getString(var10001, var9);
            Intrinsics.checkNotNull(var8);
            var10000 = var8;
        } else {
            if (rub == 0L) {
                var10001 = string.pay_to_phone_sdk_amount_part_cop;
                var9 = new Object[]{cop};
                var10000 = context.getString(var10001, var9);
            } else {
                var10001 = string.pay_to_phone_sdk_amount_part_rub_cop;
                var9 = new Object[]{rub, cop};
                var10000 = context.getString(var10001, var9);
            }

            var8 = var10000;
            Intrinsics.checkNotNull(var8);
            var10000 = var8;
        }

        return var10000;
    }

    private final Intent buildIntent(boolean isRefund, long amount, PaymentMethod paymentMethod, SoftposInfo softposInfo) {
        String action = isRefund ? "ru.tinkoff.posterminal.action.REFUND" : "ru.tinkoff.posterminal.action.PAYMENT";
        Intent var7 = new Intent(action);
        //int var9 = false;
        int var9 = 0;
        //var7.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        //var7.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        var7.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        var7.putExtra("amount", amount);
        var7.putExtra("payment_method", paymentMethod.name());
        if (isRefund) {
            if (softposInfo instanceof SoftposInfo.Nfc) {
                var7.putExtra("rrn", ((SoftposInfo.Nfc)softposInfo).getRrn());
            } else if (softposInfo instanceof SoftposInfo.Qr) {
                var7.putExtra("payment_id", ((SoftposInfo.Qr)softposInfo).getPaymentId());
            }
        }

        return var7;
    }

    // $FF: synthetic method
    static Intent buildIntent$default(SoftposManager var0, boolean var1, long var2, PaymentMethod var4, SoftposInfo var5, int var6, Object var7) {
        if ((var6 & 4) != 0) {
            var4 = PaymentMethod.UNDEFINED;
        }

        if ((var6 & 8) != 0) {
            var5 = null;
        }

        return var0.buildIntent(var1, var2, var4, var5);
    }

    // $FF: synthetic method
    public static final Callback access$getCallback$p() {
        return callback;
    }
}
