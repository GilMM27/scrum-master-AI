import { Button, Dialog, DialogContent, DialogTitle, IconButton, Typography, Box, Link } from "@mui/material";
import { CloseRounded, Telegram } from "@mui/icons-material";
import { alpha } from "@mui/material/styles";

interface TelegramChatbotDialogProps {
  open: boolean;
  onClose: () => void;
}

const TELEGRAM_LINK = "http://t.me/A008390751_bot";

const TelegramChatbotDialog = ({
  open,
  onClose,
}: TelegramChatbotDialogProps) => {
  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="xs"
      fullWidth
      sx={{
        "& .MuiDialog-paper": {
          borderRadius: 3,
          border: "1px solid",
          borderColor: "divider",
          bgcolor: "background.paper",
        },
      }}
    >
      <DialogTitle
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          pb: 1,
        }}
      >
        <Typography variant="h6" sx={{ fontWeight: 700 }}>
          Agiflow Chatbot
        </Typography>
        <IconButton
          onClick={onClose}
          size="small"
          sx={{ color: "text.secondary" }}
        >
          <CloseRounded fontSize="small" />
        </IconButton>
      </DialogTitle>

      <DialogContent dividers>
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            gap: 2.5,
            py: 2,
          }}
        >
          {/* Telegram Icon */}
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              width: 80,
              height: 80,
              borderRadius: "50%",
              bgcolor: alpha("#2AABEE", 0.15),
              border: "2px solid",
              borderColor: alpha("#2AABEE", 0.4),
            }}
          >
            <Telegram sx={{ fontSize: 44, color: "#2AABEE" }} />
          </Box>

          {/* Description */}
          <Typography
            variant="body1"
            color="text.secondary"
            align="center"
            sx={{ maxWidth: 320, lineHeight: 1.7 }}
          >
            ¿Quieres llevar la administración de tu proyecto de manera
            <strong> ágil y fácil</strong>?
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
            align="center"
            sx={{ maxWidth: 320 }}
            >
            Usa nuestro chatbot de Telegram<strong> potenciado por IA</strong> para administrar tu proyecto de manera eficiente.
          </Typography>

          {/* CTA Button */}
          <Button
            variant="contained"
            size="large"
            href={TELEGRAM_LINK}
            target="_blank"
            rel="noopener noreferrer"
            startIcon={<Telegram />}
          >
            Abrir chatbot en Telegram
          </Button>

          {/* Link */}
          <Link
            href={TELEGRAM_LINK}
            target="_blank"
            rel="noopener noreferrer"
            underline="hover"
            sx={{
              color: "#2AABEE",
              fontSize: "0.85rem",
              wordBreak: "break-all",
            }}
          >
            {TELEGRAM_LINK}
          </Link>
        </Box>
      </DialogContent>
    </Dialog>
  );
};

export default TelegramChatbotDialog;
